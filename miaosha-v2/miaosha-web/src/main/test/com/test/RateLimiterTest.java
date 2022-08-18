package com.test;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * RateLimiter测试
 *
 * @author chenh
 * @version 1.0
 * @date 2022/8/17 11:08
 **/
public class RateLimiterTest {

    /**
     * 平滑突发限流
     */
    @Test
    public void testSmoothBursty() {
        //每秒2个令牌
        RateLimiter rateLimiter = RateLimiter.create(2);
        int i = 0;
        while (i <= 100) {
            //判断能否在x毫秒内得到令牌，如果不能则立即返回false，不会阻塞程序
            if (!rateLimiter.tryAcquire(500, TimeUnit.MILLISECONDS)) {
                System.out.println("短期无法获取令牌，真不幸，排队也瞎排");
            } else {
                System.out.println("拿到令牌了");
            }
            i++;
        }
    }

    @Test
    public void testSmoothwarmingUp() {
        //每秒2个令牌 预热期为3秒
        //3秒钟之内达到原本设置的频率
        RateLimiter rateLimiter = RateLimiter.create(2, 3, TimeUnit.SECONDS);
        int i = 0;
        while (i <= 3) {
            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("end");
            i++;
        }

    }


}
