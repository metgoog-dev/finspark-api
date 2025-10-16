package com.xyz.microfinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MicrofinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicrofinanceApplication.class, args);
    }
}