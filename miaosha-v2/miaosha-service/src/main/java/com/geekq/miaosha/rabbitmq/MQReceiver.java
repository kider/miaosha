package com.geekq.miaosha.rabbitmq;

import com.geekq.api.base.AbstractResult;
import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.base.exception.GlobleException;
import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.Order;
import com.geekq.api.pojo.User;
import com.geekq.api.service.GoodsService;
import com.geekq.api.service.OrderService;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.service.MiaoshaService;
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
    GoodsService goodsService;

    @DubboReference
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(Message message, Channel channel) {
        try {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("receive msg:" + msg);
            MiaoshaMessage mm = RedisService.stringToBean(msg, MiaoshaMessage.class);
            User user = mm.getUser();
            long goodsId = mm.getGoodsId();

            Result<Goods> goodsResult = goodsService.getMsGoodsByGoodsId(goodsId);
            if (!AbstractResult.isSuccess(goodsResult)) {
                throw new GlobleException(ResultStatus.SESSION_ERROR);
            }
            Goods goods = goodsResult.getData();
            int stock = goods.getStockCount();
            if (stock <= 0) {
                return;
            }
            //判断是否已经秒杀到了
            Result<Order> orderResult = orderService.getMiaoshaOrder(Long.valueOf(user.getNickname()), goodsId);
            if (AbstractResult.isSuccess(orderResult)) {
                if (null == orderResult.getData()) {
                    //减库存 下订单 写入秒杀订单
                    long orderId = miaoshaService.miaosha(user, goods);
                    //手动 ack
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    log.info("秒杀成功,orderId:{}", orderId);
                    return;
                }
            } else {
                //直接丢弃
                try {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                    log.error("丢弃消息,已经秒杀:{}", orderResult.getStatus().getMessage());
                } catch (IOException e1) {
                    log.error(e1.getMessage());
                }
            }
        } catch (Exception e) {
            //TODO
            //可以设置最大重试次数 再次放到队列里
            //如果超过最大次数还是失败 可放到单独的“死信队列”里处理

            //直接丢弃
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                log.error("丢弃消息,秒杀失败:{}", e.getMessage());
            } catch (IOException e1) {
                log.error(e1.getMessage());
            }
        }
    }
}
