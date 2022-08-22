package com.geekq.provider.service.impl.dubbo;

import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.Order;
import com.geekq.api.pojo.User;
import com.geekq.api.service.OrderDubboService;
import com.geekq.provider.service.impl.OrderServiceImpl;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
@DubboService
public class OrderDubboServiceImpl implements OrderDubboService {

    @Autowired
    private OrderServiceImpl orderService;

    @Override
    public Result<Order> getOrderById(long orderId) {
        Order order = orderService.getOrderById(orderId);
        return Result.build(order);
    }

    @Override
    public Result<Order> createOrder(User user, Goods goods) {
        Order order = null;
        log.info("createOrder全局事务，XID = " + RootContext.getXID());
        try {
            order = orderService.createOrder(user, goods);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(ResultStatus.ORDER_CREATE_FAIL);
        }
        return Result.build(order);
    }


}
