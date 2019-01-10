package com.mars.mars_uam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Hello world!
 *
 */
@EnableSwagger2
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@EnableRedisHttpSession
@EnableDiscoveryClient
public class App {

    // 启动cas模块
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}
