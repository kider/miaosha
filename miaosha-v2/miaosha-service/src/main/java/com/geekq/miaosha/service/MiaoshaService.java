package com.geekq.miaosha.service;

import com.geekq.api.base.AbstractResult;
import com.geekq.api.base.Result;
import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.Order;
import com.geekq.api.pojo.User;
import com.geekq.api.service.GoodsService;
import com.geekq.api.service.OrderService;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miasha.redis.MiaoshaKey;
import com.geekq.miasha.utils.MD5Utils;
import com.geekq.miasha.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Slf4j
@Service
public class MiaoshaService {

    private static char[] ops = new char[]{'+', '-', '*'};

    @DubboReference
    GoodsService goodsService;
    @DubboReference
    OrderService orderService;
    @Autowired
    RedisService redisService;

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            Integer catch1 = (Integer) engine.eval(exp);
            return catch1.intValue();
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0;
        }
    }

    public void miaosha(User user, Goods goods) {
        //减库存 下订单 写入秒杀订单
        Result<Boolean> result = goodsService.reduceStock(goods);
        if (AbstractResult.isSuccess(result)) {
            Result<Order> orderResult = orderService.createOrder(user, goods);
            if (!AbstractResult.isSuccess(orderResult)) {
                log.error("创建订单失败");
                return;
            }
        }
        //如果库存不存在则内存标记为true
        setGoodsOver(goods.getId());
    }

    public long getMiaoshaResult(Long userId, long goodsId) {
        Result<Order> orderResult = orderService.getMiaoshaOrder(userId, goodsId);
        if (AbstractResult.isSuccess(orderResult)) {
            return orderResult.getData().getOrderId();
        }
        boolean isOver = getGoodsOver(goodsId);
        if (isOver) {
            return -1;
        } else {
            return 0;
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
    }

    public boolean checkPath(User user, long goodsId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, "" + user.getNickname() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    public String createMiaoshaPath(User user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        String str = MD5Utils.md5(UUIDUtil.uuid() + "123456");
        redisService.set(MiaoshaKey.getMiaoshaPath, "" + user.getNickname() + "_" + goodsId, str);
        return str;
    }

    public BufferedImage createVerifyCode(User user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getNickname() + "," + goodsId, rnd);
        //输出图片
        return image;
    }

    /**
     * 注册时用的验证码
     *
     * @param verifyCode
     * @return
     */
    public boolean checkVerifyCodeRegister(int verifyCode) {
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCodeRegister, "regitser", Integer.class);
        if (codeOld == null || codeOld - verifyCode != 0) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, "regitser");
        return true;
    }

    public BufferedImage createVerifyCodeRegister() {
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCodeRegister, "regitser", rnd);
        //输出图片
        return image;
    }

    public boolean checkVerifyCode(User user, long goodsId, int verifyCode) {
        if (user == null || goodsId <= 0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getNickname() + "," + goodsId, Integer.class);
        if (codeOld == null || codeOld - verifyCode != 0) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getNickname() + "," + goodsId);
        return true;
    }

    /**
     * + - *
     */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;
    }

}
