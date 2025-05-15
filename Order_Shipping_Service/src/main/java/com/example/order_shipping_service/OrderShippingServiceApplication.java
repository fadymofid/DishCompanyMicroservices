package com.example.order_shipping_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
@EnableRabbit

public class OrderShippingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderShippingServiceApplication.class, args);
    }

}
