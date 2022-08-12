package com.geekq.provider.mapper;

import com.geekq.api.pojo.Order;
import org.apache.ibatis.annotations.Param;


public interface OrderMapper {

    long insert(Order orderInfo);

    int insertMiaoshaOrder(Order order);

    Order getOrderById(@Param("orderId") long orderId);

}
