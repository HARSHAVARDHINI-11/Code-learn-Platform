package com.codelearn.contest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ContestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContestServiceApplication.class, args);
    }
}
