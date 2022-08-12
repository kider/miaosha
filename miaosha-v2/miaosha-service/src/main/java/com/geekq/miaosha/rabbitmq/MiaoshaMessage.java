package com.geekq.miaosha.rabbitmq;


import com.geekq.api.pojo.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MiaoshaMessage {
    private User user;
    private long goodsId;
}
