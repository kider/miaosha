package com.geekq.miaosha.redis.redismanager;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisLock {


    /**
     * @param timeout    加锁时间
     * @param unit       时间单位
     * @param key        锁KEY
     * @param requestId  客户端ID
     * @param expireTime 锁过期时间
     * @return boolean 是否成功
     * @Author kider
     * @Description 尝试加锁
     * @Date 2022/9/1 23:14
     **/
    public static boolean tryLock(long timeout, TimeUnit unit, String key, String requestId, String expireTime) {
        long nanosTimeout = unit.toNanos(timeout);
        if (nanosTimeout <= 0L) {
            return false;
        }
        final long deadline = System.nanoTime() + nanosTimeout;
        for (; ; ) {
            //lock
            if (lock(key, requestId, expireTime)) {
                return true;
            }
            nanosTimeout = deadline - System.nanoTime();
            if (nanosTimeout <= 0L) {
                return false;
            }
        }
    }


    /**
     * 加锁
     *
     * @param key        锁KEY
     * @param requestId  请求ID
     * @param expireTime 锁过期时间
     * @return {@link boolean} 是否成功
     * @author chenh
     * @date 2022/9/1 18:48
     **/
    public static boolean lock(String key, String requestId, String expireTime) {
        Jedis jedis = RedisManager.getJedis();
        String lua =
                //获取参数
                "local requestIDKey = KEYS[1] " +
                        "local currentRequestID = ARGV[1] " +
                        "local expireTimeTTL = ARGV[2] " +
                        //尝试加锁
                        "local lockSet = redis.call('hsetnx',requestIDKey,'lockKey',currentRequestID) " +
                        "if lockSet == 1 then " +
                        //加锁成功 设置过期时间和重入次数1
                        "redis.call('expire',requestIDKey,expireTimeTTL) " +
                        "redis.call('hset',requestIDKey,'lockCount',1) " +
                        "return 1 " +
                        "else " +
                        //判断是否重入加锁
                        "   local oldRequestID = redis.call('hget',requestIDKey,'lockKey') " +
                        "   if currentRequestID == oldRequestID then " +
                        //重入锁
                        "   redis.call('hincrby',requestIDKey,'lockCount',1) " +
                        //重置过期时间
                        "   redis.call('expire',requestIDKey,expireTimeTTL) " +
                        "   return 1 " +
                        "   else " +
                        //requestID不一致，加锁失败
                        "       return 0 " +
                        "   end " +
                        "end ";
        List<String> keys = new ArrayList<>();
        keys.add(key);
        List<String> args = new ArrayList<>();
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
    public static int releaseLock(String key, String requestId) {
        Jedis jedis = RedisManager.getJedis();
        String lua =
                //获取参数
                "local requestIDKey = KEYS[1] " +
                        "local currentRequestID = ARGV[1] " +
                        "if redis.call('hget',requestIDKey,'lockKey') == currentRequestID then " +
                        //requestId相同，重入次数自减
                        "    local currentCount = redis.call('hincrby',requestIDKey,'lockCount',-1) " +
                        "    if currentCount == 0 then " +
                        //重入次数为0
                        "        redis.call('del',requestIDKey) " +
                        "        return 1 " +
                        "    else " +
                        "        return 0 end " +
                        "else " +
                        "    return -1 end ";
        List<String> keys = new ArrayList<>();
        keys.add(key);
        List<String> args = new ArrayList<>();
        args.add(requestId);
        String luaScript = jedis.scriptLoad(lua);
        Long result = (Long) jedis.evalsha(luaScript, keys, args);
        return result.intValue();
    }
}
