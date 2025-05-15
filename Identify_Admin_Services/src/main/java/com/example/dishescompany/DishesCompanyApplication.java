package com.example.dishescompany;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class DishesCompanyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DishesCompanyApplication.class, args);
    }

}
