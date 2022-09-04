package com.geekq.user.service.impl.dubbo;

import com.geekq.user.redis.RedisClient;
import com.geekq.user.service.impl.UserServiceImpl;
import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.pojo.User;
import com.geekq.api.service.UserDubboService;
import com.geekq.miasha.redis.UserKey;
import com.geekq.miasha.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("userDubboServiceImpl")
public class UserDubboServiceImpl implements UserDubboService {

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Result<Boolean> getNickNameCount(String userName) {
        return Result.build(userService.getCountByUserName(userName) <= 0);
    }

    @Override
    public Result<Boolean> register(String userName, String passWord, String token) {
        Result<Boolean> result = Result.build(true);
        try {
            userService.register(userName, passWord, token);
        } catch (Exception e) {
            log.error("注册失败", e);
            result.setData(false);
        }
        return result;
    }

    @Override
    public Result<Boolean> login(String nickName, String passWord, String token) {
        User user = userService.getByNickName(nickName);
        if (user == null) {
            Result<Boolean> result = Result.error(ResultStatus.MOBILE_NOT_EXIST);
            result.setData(false);
            return result;
        }
        String dbPass = user.getPassword();
        String saltDb = user.getSalt();
        String calcPass = MD5Utils.formPassToDBPass(passWord, saltDb);
        if (!calcPass.equals(dbPass)) {
            Result<Boolean> result = Result.error(ResultStatus.PASSWORD_ERROR);
            result.setData(false);
            return result;
        }
        //写入信息
        RedisClient.set(UserKey.token.getPrefix() + token, user);
        return Result.build(true);
    }
}
