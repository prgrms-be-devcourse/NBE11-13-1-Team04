package com.springbeans.cafemenumanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CafeMenuManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CafeMenuManagementApplication.class, args);
    }

}
