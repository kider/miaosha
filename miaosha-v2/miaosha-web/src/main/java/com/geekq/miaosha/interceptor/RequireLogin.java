package com.geekq.miaosha.interceptor;

import com.geekq.api.base.enums.ResultStatus;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.geekq.api.base.enums.ResultStatus.REQUEST_ILLEGAL;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface RequireLogin {
    int seconds();

    int maxCount();

    boolean needLogin() default true;

    ResultStatus tips() default REQUEST_ILLEGAL;

}
