package com.geekq.provider.service.impl;

import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.Order;
import com.geekq.api.pojo.User;
import com.geekq.api.service.OrderService;
import com.geekq.miasha.redis.OrderKey;
import com.geekq.provider.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.Date;


@Slf4j
@DubboService
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Resource
    private RedisTemplate<String, Order> redisTemplate;

    @Override
    public Result<Order> getMiaoshaOrder(long userId, long goodsId) {
        ValueOperations<String, Order> operations = redisTemplate.opsForValue();
        String orderKey = OrderKey.getMiaoshaOrderByUidGid.getPrefix() + userId + "_" + goodsId;
        Order result = operations.get(orderKey);
        log.info("getMiaoshaOrder key:" + orderKey + ",result:{}", result);
        return Result.build(result);
    }

    @Override
    public Result<Order> getOrderById(long orderId) {
        Order order = orderMapper.getOrderById(orderId);
        return Result.build(order);
    }

    @Override
    public Result<Order> createOrder(User user, Goods goods) {
        Order orderInfo = null;
        try {
            orderInfo = new Order();
            orderInfo.setCreateDate(new Date());
            orderInfo.setDeliveryAddrId(0L);
            orderInfo.setGoodsCount(1);
            orderInfo.setGoodsId(goods.getId());
            orderInfo.setGoodsName(goods.getGoodsName());
            orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
            orderInfo.setOrderChannel(1);
            orderInfo.setStatus(0);
            orderInfo.setUserId(Long.valueOf(user.getNickname()));
            orderMapper.insert(orderInfo);
            Order order = new Order();
            order.setGoodsId(goods.getId());
            order.setOrderId(orderInfo.getId());
            order.setUserId(Long.valueOf(user.getNickname()));
            orderMapper.insertMiaoshaOrder(order);
            //缓存
            ValueOperations<String, Order> operations = redisTemplate.opsForValue();
            String orderKey = OrderKey.getMiaoshaOrderByUidGid.getPrefix() + user.getNickname() + "_" + goods.getId();
            operations.set(orderKey, order);
            log.info("createOrder key:" + orderKey + ",result:{}", order.getId());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(ResultStatus.ORDER_CREATE_FAIL);
        }
        return Result.build(orderInfo);
    }


}
