package com.ggg.marsmall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.ggg.marsmall.member.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ggg.marsmall.member.feign")
public class MarsmallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarsmallMemberApplication.class, args);
    }

}
