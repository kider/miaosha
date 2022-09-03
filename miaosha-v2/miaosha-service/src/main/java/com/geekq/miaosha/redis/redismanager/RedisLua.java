package com.geekq.miaosha.redis.redismanager;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * lua脚本使用
 */
@Slf4j
public class RedisLua {


    /**
     * 统计访问次数
     */
    public static Object getVistorCount(String key) {
        Jedis jedis = null;
        Object object = null;
        try {
            jedis = RedisManager.getJedis();

            String count =
                    "local num=redis.call('get',KEYS[1]) return num";
            List<String> keys = new ArrayList<String>();
            keys.add(key);
            List<String> argves = new ArrayList<String>();
            String luaScript = jedis.scriptLoad(count);
            object = jedis.evalsha(luaScript, keys, argves);
        } catch (Exception e) {
            log.error("统计访问次数失败！！！", e);
            return "0";
        } finally {
            RedisManager.returnJedis(jedis);
        }
        return object;
    }

    /**
     * 统计访问次数
     */
    public static void vistorCount(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisManager.getJedis();
            String count = "local num=redis.call('incr',KEYS[1]) return num";
            List<String> keys = new ArrayList<String>();
            keys.add(key);
            List<String> argves = new ArrayList<String>();
            String luaScript = jedis.scriptLoad(count);
            jedis.evalsha(luaScript, keys, argves);
        } catch (Exception e) {
            log.error("统计访问次数失败！！！", e);
        } finally {
            RedisManager.returnJedis(jedis);
        }
    }
}
