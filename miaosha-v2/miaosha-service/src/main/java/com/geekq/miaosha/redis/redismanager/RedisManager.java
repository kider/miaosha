package com.geekq.miaosha.redis.redismanager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

    private static JedisPool jedisPool;

    static {
        try {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxWaitMillis(20);
            jedisPoolConfig.setMaxIdle(10);
            jedisPool = new JedisPool(jedisPoolConfig, "www.coolboy.fun", 1234, 500, "xxxx");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Jedis getJedis() throws Exception {
        if (null != jedisPool) {
            return jedisPool.getResource();
        }
        throw new Exception("Jedispool was not init !!!");
    }


}
