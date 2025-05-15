// src/main/java/com/example/dishescompany/config/RabbitConfig.java
package com.example.dishescompany.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String CUSTOMER_EXCHANGE      = "customer-exchange";
    public static final String CUSTOMER_REQUEST_QUEUE = "customer.request.queue";
    public static final String CUSTOMER_RESPONSE_QUEUE= "customer.response.queue";
    public static final String ADMIN_EXCHANGE           = "admin-exchange";
    public static final String ROUTING_CREATE_SELLER    = "seller.create";
    public static final String ROUTING_CREATE_SHIPPING  = "shipping.create";

    @Bean
    public TopicExchange customerExchange() {
        return ExchangeBuilder
                .topicExchange(CUSTOMER_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue customerRequestQueue() {
        return QueueBuilder
                .durable(CUSTOMER_REQUEST_QUEUE)
                .build();
    }

    @Bean
    public Queue customerResponseQueue() {
        return QueueBuilder
                .durable(CUSTOMER_RESPONSE_QUEUE)
                .build();
    }

    @Bean
    public Binding customerRequestBinding(Queue customerRequestQueue, TopicExchange customerExchange) {
        return BindingBuilder
                .bind(customerRequestQueue)
                .to(customerExchange)
                .with("customer.request");
    }

    @Bean
    public Binding customerResponseBinding(Queue customerResponseQueue, TopicExchange customerExchange) {
        return BindingBuilder
                .bind(customerResponseQueue)
                .to(customerExchange)
                .with("customer.response");
    }
}





