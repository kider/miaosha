package com.geekq.miaosha.redis.redismanager;

import com.geekq.miaosha.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
public class RedisManager {

    private static JedisPool jedisPool = null;

    static {
        jedisPool = SpringUtil.getBean(JedisPool.class);
    }

    public static Jedis getJedis() throws RuntimeException {
        if (null != jedisPool) {
            return jedisPool.getResource();
        }
        throw new RuntimeException("Jedispool was not init !!!");
    }

}
