package com.example.hoteluiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HotelUiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelUiServiceApplication.class, args);
    }

}
