package com.rozhkov.callcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.rozhkov.callcenter.repository")
@ComponentScan("com.rozhkov.callcenter.entity")
public class CallCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CallCenterApplication.class, args);
    }

}
