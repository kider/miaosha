package com.geekq.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DubboProviderApplication
 * 服务提供启动类
 *
 * @author geekq
 * @date 2018/6/7
 */
@SpringBootApplication
@MapperScan("com.geekq.provider.mapper")
public class DubboProviderApplication {

    public static void main(String[] args) {
        //zookeeper 较验SASL问题
        System.setProperty("zookeeper.sasl.client", "false");
        SpringApplication.run(DubboProviderApplication.class, args);
    }
}
