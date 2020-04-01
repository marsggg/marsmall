package com.ggg.marsmall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.ggg.marsmall.coupon.dao")
@EnableDiscoveryClient
public class MarsmallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarsmallCouponApplication.class, args);
    }

}
