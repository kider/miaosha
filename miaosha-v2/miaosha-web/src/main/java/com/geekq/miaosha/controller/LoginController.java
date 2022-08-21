package com.geekq.miaosha.controller;

import com.geekq.api.base.AbstractResult;
import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.service.UserDubboService;
import com.geekq.miaosha.utils.CookieUtils;
import com.geekq.miasha.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {

    @DubboReference
    private UserDubboService userService;

    @RequestMapping("/loginin")
    @ResponseBody
    public Result<String> dologin(HttpServletResponse response, @RequestParam String nickname, @RequestParam String password) {
        String token = UUIDUtils.uuid();
        Result<Boolean> result = userService.login(nickname, password, token);
        if (AbstractResult.isSuccess(result)) {
            Boolean flag = result.getData();
            //返回错误信息
            if (!flag) {
                return Result.build(result.getMessage());
            }
            //写入Cookie
            CookieUtils.addUserCookie(response, token);
            return Result.build();
        }
        return Result.error(ResultStatus.EXCEPTION);
    }
}
