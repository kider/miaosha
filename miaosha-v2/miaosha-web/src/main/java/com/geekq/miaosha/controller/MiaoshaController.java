package com.geekq.miaosha.controller;

import com.geekq.api.base.Result;
import com.geekq.api.pojo.User;
import com.geekq.miaosha.interceptor.RequireLogin;
import com.geekq.miaosha.service.MiaoshaService;
import com.geekq.miaosha.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

import static com.geekq.api.base.enums.ResultStatus.*;


@Slf4j
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    MiaoshaService miaoshaService;

    /**
     * 立即秒杀
     *
     * @param user
     * @param path
     * @param goodsId
     * @return {@link Result< Integer>}
     * @author chenh
     * @date 2022/8/15 14:06
     **/
    @RequireLogin(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/{path}/miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(HttpServletRequest request, User user, @PathVariable("path") String path,
                                   @RequestParam("goodsId") long goodsId) {
        String ip = IpUtils.getRemoteIp(request);
        return miaoshaService.miaosha(user, goodsId, path, ip);
    }


    /**
     * 获取秒杀结果
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     *
     * @param model
     * @param user
     * @param goodsId
     * @return {@link Result< Long>}
     * @author chenh
     * @date 2022/8/15 14:31
     **/
    @RequireLogin(seconds = 5, maxCount = 10, tips = ORDER_WAIT_TIPS)
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, User user,
                                      @RequestParam("goodsId") long goodsId) {
        Result<Long> result = Result.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        model.addAttribute("user", user);
        Long miaoshaResult = miaoshaService.getMiaoshaResult(Long.valueOf(user.getNickname()), goodsId);
        result.setData(miaoshaResult);
        return result;
    }

    /**
     * 刷新验证码
     *
     * @param response
     * @param user
     * @param goodsId
     * @return {@link Result< String>}
     * @author chenh
     * @date 2022/8/15 15:40
     **/
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, User user,
                                               @RequestParam("goodsId") long goodsId) {
        Result<String> result = Result.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        try {
            BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return result;
        } catch (Exception e) {
            log.error("生成验证码错误-----goodsId:{}", goodsId, e);
            result.withError(MIAOSHA_FAIL.getCode(), MIAOSHA_FAIL.getMessage());
            return result;
        }
    }


    /**
     * 验证验证码值并返回path
     *
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return {@link Result< String>}
     * @author chenh
     * @date 2022/8/15 16:44
     **/
    @RequireLogin(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(User user,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode
    ) {
        Result<String> result = Result.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            result.withError(REQUEST_ILLEGAL.getCode(), REQUEST_ILLEGAL.getMessage());
            return result;
        }
        String path = miaoshaService.createMiaoshaPath(user, goodsId);
        result.setData(path);
        return result;
    }
}
