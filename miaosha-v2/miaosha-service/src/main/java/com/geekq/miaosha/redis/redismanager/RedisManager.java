package com.geekq.miaosha.redis.redismanager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

    private static JedisPool jedisPool;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxWaitMillis(20);
        jedisPoolConfig.setMaxIdle(10);
        jedisPool = new JedisPool(jedisPoolConfig, "www.coolboy.fun", 1234, 500, "xxxx");
    }

    public static Jedis getJedis() throws RuntimeException {
        if (null != jedisPool) {
            return jedisPool.getResource();
        }
        throw new RuntimeException("Jedispool was not init !!!");
    }


}
