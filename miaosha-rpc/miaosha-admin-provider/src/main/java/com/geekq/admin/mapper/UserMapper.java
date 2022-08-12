package com.geekq.admin.mapper;


import com.geekq.api.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    User getByNickname(@Param("nickname") String nickname);

    User getById(@Param("id") long id);

    void update(User user);

    void insertUser(User user);

    int getCountByUserName(@Param("userName") String userName, @Param("userType") int userType);

}
