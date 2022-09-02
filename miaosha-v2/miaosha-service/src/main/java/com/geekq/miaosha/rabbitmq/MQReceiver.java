package com.geekq.miaosha.rabbitmq;

import com.geekq.api.base.AbstractResult;
import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.base.exception.GlobleException;
import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.Order;
import com.geekq.api.pojo.User;
import com.geekq.api.service.GoodsDubboService;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.service.MiaoshaService;
import com.geekq.miasha.redis.GoodsKey;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class MQReceiver {

    @Autowired
    RedisService redisService;

    @DubboReference
    GoodsDubboService goodsService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(Message message, Channel channel) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("receive msg:" + msg);
        MiaoshaMessage mm = RedisService.stringToBean(msg, MiaoshaMessage.class);
        User user = mm.getUser();
        long goodsId = mm.getGoodsId();
        try {
            //先判断是否已经秒杀成功了
            Order order = miaoshaService.getMiaoshaOrder(Long.parseLong(user.getNickname()), goodsId);
            if (null == order) {
                Result<Goods> goodsResult = goodsService.getMsGoodsByGoodsId(goodsId);
                if (!AbstractResult.isSuccess(goodsResult)) {
                    throw new GlobleException(ResultStatus.GOODS_GET_FAIL);
                }
                Goods goods = goodsResult.getData();
                int stock = goods.getStockCount();
                //用缓存中的库存做判断 TODO 暂时不行 拿不到商品其他信息
                //Long stock = redisService.get(GoodsKey.getMiaoshaGoodsStock, "" + goodsId, Long.class);
                if (stock <= 0) {
                    log.error("nickname:{},goodsId:{},秒杀失败,没有库存了", user.getNickname(), goodsId);
                    return;
                }
                //减库存 下订单
                long orderId = miaoshaService.createMsOrder(user, goods);
                if (-1 == orderId) {
                    log.error("已经没有库存了,扣减库存失败:nickname:{},goodsId:{}", user.getNickname(), goodsId);
                }
                //手动 ack
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                log.info("秒杀成功,orderId:{}", orderId);
            } else {
                //直接丢弃
                try {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                    log.error("丢弃消息,已经秒杀成功了,userId:{},goodsId:{}", user.getNickname(), goodsId);
                } catch (IOException e1) {
                    log.error(e1.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("创建订单失败 userId:{},goodsId:{},", user.getNickname(), goodsId, e);
            //订单失败的时候缓存库存+1
            redisService.incr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
            //TODO
            //可以设置最大重试次数 再次放到队列里
            //如果超过最大次数还是失败 可放到单独的“死信队列”里处理
            //直接丢弃
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                log.error("丢弃消息,秒杀失败:{}", e.getMessage());
            } catch (IOException ioException) {
                log.error(ioException.getMessage());
            }
        }
    }
}
