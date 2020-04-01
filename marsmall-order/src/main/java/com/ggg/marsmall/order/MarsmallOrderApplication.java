package com.ggg.marsmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.ggg.marsmall.order.dao")
@EnableDiscoveryClient
public class MarsmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarsmallOrderApplication.class, args);
    }

}
