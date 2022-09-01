package com.geekq.miaosha.redis.redismanager;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class RedisLock {

    /**
     * 加锁
     *
     * @param key        锁KEY
     * @param requestId  请求ID
     * @param expireTime 锁过期时间
     * @return {@link boolean}
     * @author chenh
     * @date 2022/9/1 18:48
     **/
    public boolean getLock(String key, String requestId, String expireTime) {
        Jedis jedis = RedisManager.getJedis();
        String lua = "" +
                "-- 获取参数" +
                "local requestIDKey = KEYS[1]" +
                "local currentRequestID = ARGV[1]" +
                "local expireTimeTTL = ARGV[2]" +
                "-- 尝试加锁" +
                "local lockSet = redis.call('hsetnx',requestIDKey,'lockKey',currentRequestID)" +
                "if lockSet == 1" +
                "then" +
                "    -- 加锁成功 设置过期时间和重入次数1" +
                "     redis.call('expire',requestIDKey,expireTimeTTL)" +
                "     redis.call('hset',requestIDKey,'lockCount',1)" +
                "        return 1" +
                "else" +
                "    -- 判断是否重入加锁" +
                "    local oldRequestID = redis.call('hget',requestIDKey,'lockKey')" +
                "    if currentRequestID == oldRequestID" +
                "    then" +
                "        -- 重入锁" +
                "        redis.call('hincrby',requestIDKey,'lockCount',1)" +
                "        -- 重置过期时间" +
                "        redis.call('expire',requestIDKey,expireTimeTTL)" +
                "        return 1" +
                "    else" +
                "        -- requestID不一致，加锁失败" +
                "        return 0" +
                "    end" +
                "end";
        List<String> keys = new ArrayList<String>();
        keys.add(key);
        List<String> args = new ArrayList<String>();
        args.add(requestId);
        args.add(expireTime);
        String luaScript = jedis.scriptLoad(lua);
        Long result = (Long) jedis.evalsha(luaScript, keys, args);
        return result == 1;
    }

    /**
     * 加锁
     *
     * @param key       锁KEY
     * @param requestId 请求ID
     * @return {@link boolean}
     * @author chenh
     * @date 2022/9/1 18:48
     **/
    public boolean releaseLock(String key, String requestId) {

        Jedis jedis = RedisManager.getJedis();
        String lua = "-- 获取参数" +
                "local requestIDKey = KEYS[1]" +
                "local currentRequestID = ARGV[1]" +
                "if redis.call('hget',requestIDKey,'lockKey') == currentRequestID" +
                "then" +
                "    -- requestId相同，重入次数自减" +
                "    local currentCount = redis.call('hincrby',requestIDKey,'lockCount',-1)" +
                "    if currentCount == 0" +
                "    then" +
                "        -- 重入次数为0" +
                "        redis.call('del',requestIDKey)" +
                "        return 1" +
                "    else" +
                "        return 0 end" +
                "else" +
                "    return 0 end";
        List<String> keys = new ArrayList<String>();
        keys.add(key);
        List<String> args = new ArrayList<String>();
        args.add(requestId);
        String luaScript = jedis.scriptLoad(lua);
        Long result = (Long) jedis.evalsha(luaScript, keys, args);
        return result == 1;
    }
}
