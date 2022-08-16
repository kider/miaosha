package com.geekq.admin.service.impl;

import com.geekq.admin.mapper.UserMapper;
import com.geekq.admin.redis.RedisClient;
import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.pojo.User;
import com.geekq.api.service.UserService;
import com.geekq.miasha.redis.UserKey;
import com.geekq.miasha.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.geekq.miasha.constants.Constants.USERTYPE_NORMAL;

@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<Boolean> getNickNameCount(String userName) {
        return Result.build(userMapper.getCountByUserName(userName, USERTYPE_NORMAL) <= 0);
    }

    @Override
    public Result<Boolean> register(String userName, String passWord, String token) {
        Result<Boolean> result = Result.build(true);
        User user = new User();
        user.setNickname(userName);
        //password  应该在前段进行一次MD5 在后端在进行一个MD5 在入库
        String salt = MD5Utils.getSaltT();
        String md5passWord = MD5Utils.formPassToDBPass(passWord, salt);
        user.setPassword(md5passWord);
        user.setRegisterDate(new Date());
        user.setSalt(salt);
        try {
            userMapper.insertUser(user);
            //写入信息
            RedisClient.set(UserKey.token.getPrefix() + token, user);
        } catch (Exception e) {
            log.error("注册失败", e);
            result.setData(false);
        }
        return result;
    }

    @Override
    public Result<Boolean> login(String nickName, String passWord, String token) {
        User user = getByNickName(nickName);
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

    private User getByNickName(String nickName) {
        //取缓存
        User user = RedisClient.get(UserKey.getByNickName.getPrefix() + nickName, User.class);
        if (user != null) {
            return user;
        }
        //取数据库
        user = userMapper.getByNickname(nickName);
        if (user != null) {
            RedisClient.set(UserKey.getByNickName.getPrefix() + nickName, user);
            return user;
        }
        return null;
    }
}
