package com.geekq.miaosha.interceptor;

import com.geekq.miasha.redis.BasePrefix;

public class AccessKey extends BasePrefix {

    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey withExpire(int expireSeconds) {
        return new AccessKey(expireSeconds, "interceptor");
    }

}
