package com.geekq.miaosha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GeekQMainApplication {

    public static void main(String[] args) throws Exception {
        //zookeeper 较验SASL问题
        System.setProperty("zookeeper.sasl.client", "false");
        SpringApplication.run(GeekQMainApplication.class, args);
    }


}
