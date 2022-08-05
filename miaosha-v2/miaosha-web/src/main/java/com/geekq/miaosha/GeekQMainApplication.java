package com.geekq.miaosha;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.geekq.miaosha.mapper")
public class GeekQMainApplication {

    public static void main(String[] args) throws Exception {
        //zookeeper 较验SASL问题
        System.setProperty("zookeeper.sasl.client", "false");
        SpringApplication.run(GeekQMainApplication.class, args);
    }


}
