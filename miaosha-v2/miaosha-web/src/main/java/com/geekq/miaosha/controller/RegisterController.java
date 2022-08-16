package com.geekq.miaosha.controller;

import com.geekq.api.base.AbstractResult;
import com.geekq.api.base.Result;
import com.geekq.api.service.UserService;
import com.geekq.miaosha.redis.redismanager.RedisLua;
import com.geekq.miaosha.service.RegisterService;
import com.geekq.miaosha.utils.CookieUtils;
import com.geekq.miasha.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

import static com.geekq.api.base.enums.ResultStatus.MIAOSHA_FAIL;
import static com.geekq.api.base.enums.ResultStatus.RESIGETER_FAIL;
import static com.geekq.miasha.constants.Constants.COUNTLOGIN;


@Slf4j
@Controller
@RequestMapping("/")
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private RegisterService registerService;


    /**
     * 登录页面
     */
    @RequestMapping("/login")
    public String loginIndex(Model model) {
        RedisLua.vistorCount(COUNTLOGIN);
        String count = RedisLua.getVistorCount(COUNTLOGIN).toString();
        log.info("访问网站的次数为:{}", count);
        model.addAttribute("count", count);
        return "login";
    }

    /**
     * 注册页面
     */
    @RequestMapping("/to_register")
    public String register() {
        return "register";
    }


    /**
     * 校验用户
     */
    @RequestMapping("/checkUsername")
    @ResponseBody
    public Result<Boolean> checkUsername(String username) {
        return userService.getNickNameCount(username);
    }

    /**
     * 注册用户
     *
     * @param userName
     * @param passWord
     */
    @RequestMapping("/register")
    @ResponseBody
    public Result<Boolean> register(@RequestParam("username") String userName,
                                    @RequestParam("password") String passWord,
                                    HttpServletResponse response,
                                    HttpServletRequest request) {
        String token = UUIDUtils.uuid();
        Boolean isR = false;
        Result<Boolean> result = userService.register(userName, passWord, token);
        if (AbstractResult.isSuccess(result)) {
            isR = result.getData();
        }
        if (!isR) {
            result.withError(RESIGETER_FAIL.getCode(), RESIGETER_FAIL.getMessage());
            result.setData(false);
            return result;
        }
        //写入Cookie
        CookieUtils.addUserCookie(response, token);
        result.setData(true);
        return result;
    }

    @RequestMapping(value = "/verifyCodeRegister", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getRegisterVerifyCode(HttpServletResponse response
    ) {
        Result<String> result = Result.build();
        try {
            BufferedImage image = registerService.createVerifyCodeRegister();
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return result;
        } catch (Exception e) {
            log.error("注册用户生成验证码错误:{}", e);
            result.withError(MIAOSHA_FAIL.getCode(), MIAOSHA_FAIL.getMessage());
            return result;
        }
    }
}
