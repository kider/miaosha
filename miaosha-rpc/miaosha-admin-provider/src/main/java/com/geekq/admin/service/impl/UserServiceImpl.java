package com.geekq.admin.service.impl;

import com.geekq.admin.mapper.UserMapper;
import com.geekq.admin.redis.RedisClient;
import com.geekq.api.pojo.User;
import com.geekq.miasha.redis.UserKey;
import com.geekq.miasha.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.geekq.miasha.constants.Constants.USERTYPE_NORMAL;

@Slf4j
@Service
public class UserServiceImpl {

    @Autowired
    private UserMapper userMapper;

    public int getCountByUserName(String userName) {
        return userMapper.getCountByUserName(userName, USERTYPE_NORMAL);
    }

    public void register(String userName, String passWord, String token) throws Exception {
        User user = new User();
        user.setNickname(userName);
        //password  应该在前段进行一次MD5 在后端在进行一个MD5 在入库
        String salt = MD5Utils.getSaltT();
        String md5passWord = MD5Utils.formPassToDBPass(passWord, salt);
        user.setPassword(md5passWord);
        user.setRegisterDate(new Date());
        user.setSalt(salt);
        userMapper.insertUser(user);
        //写入信息
        RedisClient.set(UserKey.token.getPrefix() + token, user);
    }

    public User getByNickName(String nickName) {
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
