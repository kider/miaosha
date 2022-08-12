package com.geekq.miasha.vo;

import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {
    private Goods goods;
    private Order order;
}
