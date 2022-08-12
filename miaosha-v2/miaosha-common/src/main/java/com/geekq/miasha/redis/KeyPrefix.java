package com.geekq.miasha.redis;

public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();

}
