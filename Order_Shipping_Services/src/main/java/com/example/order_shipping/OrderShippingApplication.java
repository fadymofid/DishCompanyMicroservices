package com.example.order_shipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;  // ← Netflix-specific
@SpringBootApplication
@EnableEurekaClient   // For Eureka registration
public class OrderShippingApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderShippingApplication.class, args);
    }
}