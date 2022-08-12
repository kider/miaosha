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
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void receive(String message) {
        log.info("receive message:" + message);
        MiaoshaMessage mm = RedisService.stringToBean(message, MiaoshaMessage.class);
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
            //减库存 下订单 写入秒杀订单
            miaoshaService.miaosha(user, goods);
        }
    }
}
