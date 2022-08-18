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
    public static boolean accquire() throws Exception {
        Jedis jedis = RedisManager.getJedis();
        String lua =
                //限流KEY
                "local key = KEYS[1] " +
                        //规则
                        "local limit = tonumber(ARGV[1]) " +
                        //当前次数
                        "local current = tonumber(redis.call('get', key) or '0') " +
                        "if current + 1 > limit " +
                        //如果超出限流大小
                        "then return 0 " +
                        "else " +
                        //请求数+1 并设置2秒过期
                        "redis.call('INCRBY', key,'1') " +
                        "redis.call('expire', key,'2') " +
                        "end return 1 ";
        //当前秒
        String key = "ip:" + System.currentTimeMillis() / 1000;
        //最大限制
        String limit = "3";
        List<String> keys = new ArrayList<String>();
        keys.add(key);
        List<String> args = new ArrayList<String>();
        args.add(limit);
        String luaScript = jedis.scriptLoad(lua);
        Long result = (Long) jedis.evalsha(luaScript, keys, args);
        return result == 1;
    }
}
