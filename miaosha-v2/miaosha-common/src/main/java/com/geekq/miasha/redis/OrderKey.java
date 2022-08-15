package com.geekq.miasha.redis;

public class OrderKey extends BasePrefix {

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");

    public static OrderKey getOrderDetail = new OrderKey(60, "od");

    private OrderKey(String prefix) {
        super(prefix);
    }

    private OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
