package com.geekq.miaosha.controller;

import com.geekq.api.base.AbstractResult;
import com.geekq.api.base.Result;
import com.geekq.api.service.UserService;
import com.geekq.miaosha.redis.redismanager.RedisLua;
import com.geekq.miaosha.utils.CookieUtils;
import com.geekq.miasha.utils.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.geekq.api.base.enums.ResultStatus.RESIGETER_FAIL;
import static com.geekq.miasha.enums.Constanst.COUNTLOGIN;


@Controller
@RequestMapping("/")
public class RegisterController {

    private static Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;


    /**
     * 登录页面
     */
    @RequestMapping("/login")
    public String loginIndex(Model model) {
        RedisLua.vistorCount(COUNTLOGIN);
        String count = RedisLua.getVistorCount(COUNTLOGIN).toString();
        logger.info("访问网站的次数为:{}", count);
        model.addAttribute("count", count);
        return "login";
    }

    /**
     * 注册页面
     *
     * @return
     */
    @RequestMapping("/to_register")
    public String register() {
        return "register";
    }


    /**
     * 校验程序
     *
     * @return
     */
    @RequestMapping("/checkUsername")
    @ResponseBody
    public Result<Boolean> checkUsername(String username) {
        return userService.getNickNameCount(username);
    }

    /**
     * 注册网站
     *
     * @param userName
     * @param passWord
     * @return
     */
    @RequestMapping("/register")
    @ResponseBody
    public Result<Boolean> register(@RequestParam("username") String userName,
                                    @RequestParam("password") String passWord,
                                    HttpServletResponse response,
                                    HttpServletRequest request) {
        String token = UUIDUtil.uuid();
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
}
