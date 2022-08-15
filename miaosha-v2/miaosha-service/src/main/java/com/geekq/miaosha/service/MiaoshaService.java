package com.geekq.miaosha.service;

import com.geekq.api.base.AbstractResult;
import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.base.exception.GlobleException;
import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.Order;
import com.geekq.api.pojo.User;
import com.geekq.api.service.GoodsService;
import com.geekq.api.service.OrderService;
import com.geekq.miaosha.rabbitmq.MQSender;
import com.geekq.miaosha.rabbitmq.MiaoshaMessage;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.redis.redismanager.RedisLimitRateWithLUA;
import com.geekq.miasha.redis.GoodsKey;
import com.geekq.miasha.redis.MiaoshaKey;
import com.geekq.miasha.utils.MD5Utils;
import com.geekq.miasha.utils.UUIDUtils;
import com.geekq.miasha.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.script.ScriptException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import static com.geekq.api.base.enums.ResultStatus.*;

@Slf4j
@Service
public class MiaoshaService implements InitializingBean {

    @DubboReference
    GoodsService goodsService;
    @DubboReference
    OrderService orderService;
    @Autowired
    RedisService redisService;
    @Autowired
    MQSender mqSender;

    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * 立即秒杀
     * 订单通过mq服务生成
     *
     * @param user
     * @param goodsId
     * @param path
     * @return {@link Result< Integer>}
     * @author chenh
     * @date 2022/8/15 14:16
     **/
    public Result<Integer> miaosha(User user, Long goodsId, String path) {

        Result<Integer> result = Result.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }

        //验证path
        boolean check = checkPath(user, goodsId, path);
        if (!check) {
            result.withError(REQUEST_ILLEGAL.getCode(), REQUEST_ILLEGAL.getMessage());
            return result;
        }

        //使用RateLimiter 限流
        /*
        RateLimiter rateLimiter = RateLimiter.create(10);
        //判断能否在1秒内得到令牌，如果不能则立即返回false，不会阻塞程序
        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
            System.out.println("短期无法获取令牌，真不幸，排队也瞎排");
            return ResultGeekQ.error(MIAOSHA_FAIL);
        }*/

        /**
         * 分布式限流
         */
        try {
            RedisLimitRateWithLUA.accquire();
        } catch (IOException e) {
            result.withError(EXCEPTION.getCode(), REPEATE_MIAOSHA.getMessage());
            return result;
        } catch (URISyntaxException e) {
            result.withError(EXCEPTION.getCode(), REPEATE_MIAOSHA.getMessage());
            return result;
        }

        //是否已经秒杀到
        Result<Order> orderResult = orderService.getMiaoshaOrder(Long.parseLong(user.getNickname()), goodsId);
        if (!AbstractResult.isSuccess(orderResult)) {
            result.withError(EXCEPTION.getCode(), REPEATE_MIAOSHA.getMessage());
            return result;
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            result.withError(EXCEPTION.getCode(), MIAO_SHA_OVER.getMessage());
            return result;
        }
        //预见库存 TODO
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            result.withError(EXCEPTION.getCode(), MIAO_SHA_OVER.getMessage());
            return result;
        }
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setGoodsId(goodsId);
        mm.setUser(user);
        mqSender.sendMiaoshaMessage(mm);
        return result;
    }

    /**
     * 获取秒杀结果
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     *
     * @param userId  用户ID
     * @param goodsId 秒杀商品ID
     * @return {@link long}
     * @author chenh
     * @date 2022/8/15 14:32
     **/
    public long getMiaoshaResult(Long userId, long goodsId) {
        Result<Order> orderResult = orderService.getMiaoshaOrder(userId, goodsId);
        if (AbstractResult.isSuccess(orderResult)) {
            Order order = orderResult.getData();
            if (null != order) {
                return order.getOrderId();
            }
        }
        boolean isOver = getGoodsOver(goodsId);
        if (isOver) {
            return -1;
        } else {
            //前端继续轮询
            return 0;
        }
    }

    /**
     * 减库存，创建订单
     *
     * @param user
     * @param goods
     * @return {@link long}
     * @author chenh
     * @date 2022/8/15 14:54
     **/
    public long createMsOrder(User user, Goods goods) {
        //减库存 下订单 写入秒杀订单
        //TODO 分布式的情况下应该怎么减库存
        Result<Boolean> result = goodsService.reduceStock(goods);
        if (AbstractResult.isSuccess(result)) {
            if (result.getData()) {
                Result<Order> orderResult = orderService.createOrder(user, goods);
                if (!AbstractResult.isSuccess(orderResult)) {
                    //TODO 如果创建订单失败是否需要处理库存
                    throw new GlobleException(ResultStatus.ORDER_CREATE_FAIL);
                }
                return orderResult.getData().getId();
            } else {
                //如果没有库存则标记为true
                setGoodsOver(goods.getId());
            }
        }
        return -1;
    }

    /**
     * 初始化库存信息
     *
     * @author chenh
     * @date 2022/8/15 14:58
     **/
    @Override
    public void afterPropertiesSet() throws Exception {
        Result<List<Goods>> result = goodsService.list();
        if (!CollectionUtils.isEmpty(result.getData())) {
            for (Goods goods : result.getData()) {
                redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
                localOverMap.put(goods.getId(), false);
            }
        }
    }


    /**
     * 获取秒杀验证码
     *
     * @param user
     * @param goodsId
     * @return {@link BufferedImage}
     * @author chenh
     * @date 2022/8/15 15:41
     **/
    public BufferedImage createVerifyCode(User user, long goodsId) throws ScriptException {
        if (user == null || goodsId <= 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        String verifyCode = VerifyCodeUtils.generateVerifyCode();
        BufferedImage image = VerifyCodeUtils.createVerifyCodeImg(verifyCode, width, height);
        int rnd = VerifyCodeUtils.calc(verifyCode);
        //把验证码存到redis中
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getNickname() + "_" + goodsId, rnd);
        //输出图片
        return image;
    }

    /**
     * 较验秒杀验证码
     *
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return {@link boolean}
     * @author chenh
     * @date 2022/8/15 16:49
     **/
    public boolean checkVerifyCode(User user, long goodsId, int verifyCode) {
        if (user == null || goodsId <= 0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getNickname() + "_" + goodsId, Integer.class);
        if (codeOld == null || codeOld - verifyCode != 0) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getNickname() + "_" + goodsId);
        return true;
    }

    /**
     * 创建秒杀path
     *
     * @param user
     * @param goodsId
     * @return {@link String}
     * @author chenh
     * @date 2022/8/15 16:50
     **/
    public String createMiaoshaPath(User user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        String str = MD5Utils.md5(UUIDUtils.uuid());
        redisService.set(MiaoshaKey.getMiaoshaPath, "" + user.getNickname() + "_" + goodsId, str);
        return str;
    }

    /**
     * 验证秒杀path
     *
     * @param user
     * @param goodsId
     * @param path
     * @return {@link boolean}
     * @author chenh
     * @date 2022/8/15 16:49
     **/
    private boolean checkPath(User user, long goodsId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, "" + user.getNickname() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }


    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
    }

}
