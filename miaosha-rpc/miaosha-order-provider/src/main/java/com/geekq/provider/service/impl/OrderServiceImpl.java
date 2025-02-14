package com.geekq.provider.service.impl;

import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.Order;
import com.geekq.api.pojo.User;
import com.geekq.provider.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;


@Slf4j
@Service
public class OrderServiceImpl {

    @Autowired
    private OrderMapper orderMapper;

    @Resource
    private RedisTemplate<String, Order> redisTemplate;

    public Order getOrderById(long orderId) {
        Order order = orderMapper.getOrderById(orderId);
        return order;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Order createOrder(User user, Goods goods) throws Exception {
        Order orderInfo = new Order();
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
        return orderInfo;
    }


}
