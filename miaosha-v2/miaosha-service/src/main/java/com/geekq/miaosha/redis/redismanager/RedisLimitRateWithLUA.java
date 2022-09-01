package com.geekq.miaosha.redis.redismanager;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

public class RedisLimitRateWithLUA {

    /**
     * 分布式限流
     *
     * @return {@link boolean}
     * @author chenh
     * @date 2022/8/17 15:59
     **/
    public static boolean accquire(String ip) throws Exception {
        Jedis jedis = RedisManager.getJedis();
        String lua =    //限流KEY
                        "local key = KEYS[1] " +
                        //规则
                        "local limit = tonumber(ARGV[1]) " +
                        "local expire_time = ARGV[2] " +
                        "local is_exists = redis.call('EXISTS', key) " +
                        "if is_exists == 1 then " +
                        "    if redis.call('INCR', key) > limit then " +
                        "        return 0 " +
                        "    else" +
                        "        return 1 end " +
                        "else " +
                        "    redis.call('SET', key, 1) " +
                        "    redis.call('EXPIRE', key, expire_time) " +
                        "    return 1 " +
                        "end ";
        //IP
        String key = "ip:" + ip;
        List<String> keys = new ArrayList<>();
        keys.add(key);
        List<String> args = new ArrayList<>();
        //最大限制 同一ip每2秒最多3次
        String limit = "3";
        args.add(limit);
        //过期时间
        String expireTime = "2";
        args.add(expireTime);
        String luaScript = jedis.scriptLoad(lua);
        Long result = (Long) jedis.evalsha(luaScript, keys, args);
        return result == 1;
    }
}
