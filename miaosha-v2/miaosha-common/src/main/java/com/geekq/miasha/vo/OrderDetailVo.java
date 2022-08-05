package com.geekq.miasha.vo;

import com.geekq.api.entity.GoodsVoOrder;
import com.geekq.miasha.entity.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {
    private GoodsVoOrder goods;
    private OrderInfo order;

    public GoodsVoOrder getGoods() {
        return goods;
    }

    public void setGoods(GoodsVoOrder goods) {
        this.goods = goods;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}
