package com.geekq.miaosha.service;

import com.geekq.miaosha.redis.RedisService;
import com.geekq.miasha.redis.MiaoshaKey;
import com.geekq.miasha.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.awt.image.BufferedImage;

/**
 * 注册
 *
 * @author chenh
 * @version 1.0
 * @date 2022/8/15 16:07
 **/
@Slf4j
@Service
public class RegisterService {

    @Autowired
    RedisService redisService;

    public BufferedImage createVerifyCodeRegister() throws ScriptException {
        int width = 80;
        int height = 32;
        String verifyCode = VerifyCodeUtils.generateVerifyCode();
        BufferedImage image = VerifyCodeUtils.createVerifyCodeImg(verifyCode, width, height);
        int rnd = VerifyCodeUtils.calc(verifyCode);
        //把验证码存到redis中
        redisService.set(MiaoshaKey.getMiaoshaVerifyCodeRegister, "regitser", rnd);
        //输出图片
        return image;
    }

    public boolean checkVerifyCodeRegister(int verifyCode) {
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCodeRegister, "regitser", Integer.class);
        if (codeOld == null || codeOld - verifyCode != 0) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, "regitser");
        return true;
    }

}
