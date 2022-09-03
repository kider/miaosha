package com.geekq.admin.redis;

import com.alibaba.fastjson.JSON;
import com.geekq.admin.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
public class RedisClient {
    /**
     * 池化管理jedis链接池
     */
    public static JedisPool jedisPool;

    static {
        jedisPool = SpringUtil.getBean(JedisPool.class);
    }

    /**
     * 向缓存中设置字符串内容
     *
     * @param key   key
     * @param value value
     * @return
     * @throws Exception
     */
    public static boolean set(String key, String value) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * 向缓存中设置对象
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean set(String key, Object value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String objectJson = JSON.toJSONString(value);
            jedis.set(key, objectJson);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * 删除缓存中得对象，根据key
     *
     * @param key
     * @return
     */
    public static boolean del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     * @return
     */
    public static Object get(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Object value = jedis.get(key);
            return value;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }
    }


    /**
     * 根据key 获取对象
     *
     * @param key
     * @return
     */
    public static <T> T get(String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String value = jedis.get(key);
            return JSON.parseObject(value, clazz);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (null != jedis) {
                jedisPool.returnResource(jedis);
            }
        }
        return null;
    }
}