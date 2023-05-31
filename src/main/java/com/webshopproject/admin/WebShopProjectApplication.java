package com.webshopproject.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.webshopproject.entity")
public class WebShopProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebShopProjectApplication.class, args);
    }

}
