package com.geekq.api.service;

import com.geekq.api.base.Result;
import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.Order;
import com.geekq.api.pojo.User;

/**
 * 订单Dubbo服务
 */
public interface OrderDubboService {

    Result<Order> getOrderById(long orderId);

    Result<Order> createOrder(User user, Goods goods);

}
