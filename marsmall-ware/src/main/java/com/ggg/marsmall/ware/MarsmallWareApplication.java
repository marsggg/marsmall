package com.ggg.marsmall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.ggg.marsmall.ware.dao")
@EnableDiscoveryClient
public class MarsmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarsmallWareApplication.class, args);
    }

}
