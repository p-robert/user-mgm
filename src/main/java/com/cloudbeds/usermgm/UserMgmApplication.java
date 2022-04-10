package com.cloudbeds.usermgm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
public class UserMgmApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserMgmApplication.class, args);
    }

}