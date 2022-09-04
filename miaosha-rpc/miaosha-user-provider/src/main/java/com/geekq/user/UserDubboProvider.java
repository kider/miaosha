package com.geekq.user;


import com.alibaba.dubbo.container.Main;

/**
 * @ClassName UserDubboProvider
 * @Description 启动类
 * @Author kider
 * @Version 1.0
 **/
public class UserDubboProvider {

    public static void main(String[] args) {
        //zookeeper 较验SASL问题
        System.setProperty("zookeeper.sasl.client", "false");
        Main.main(args);
    }


}
