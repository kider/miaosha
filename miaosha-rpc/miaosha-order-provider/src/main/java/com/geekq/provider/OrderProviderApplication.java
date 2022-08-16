package com.geekq.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * OrderProviderApplication
 * 服务提供启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.geekq.provider.mapper")
public class OrderProviderApplication {

    public static void main(String[] args) {
        //zookeeper 较验SASL问题
        System.setProperty("zookeeper.sasl.client", "false");
        SpringApplication.run(OrderProviderApplication.class, args);
    }
}
