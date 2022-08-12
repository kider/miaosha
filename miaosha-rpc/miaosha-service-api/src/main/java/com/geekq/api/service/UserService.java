package com.geekq.api.service;

import com.geekq.api.base.Result;

public interface UserService {

    Result<Boolean> getNickNameCount(String userName);

    Result<Boolean> register(String userName, String passWord, String token);

    Result<Boolean> login(String nickName, String passWord, String token);

}
