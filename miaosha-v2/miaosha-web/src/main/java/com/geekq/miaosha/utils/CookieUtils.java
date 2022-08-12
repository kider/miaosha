package com.geekq.miaosha.utils;

import com.geekq.miasha.redis.UserKey;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie
 *
 * @author chenh
 * @version 1.0
 * @date 2022/8/11 11:20
 **/
public class CookieUtils {

    public static final String COOKIE_NAME_TOKEN = "token";

    public static void addUserCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //设置有效期
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
