package com.example.dishescompany.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
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

    @Bean
    public TopicExchange adminExchange() {
        return ExchangeBuilder
                .topicExchange(ADMIN_EXCHANGE)
                .durable(true)
                .build();
    }
    
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

    @Bean
    public Queue adminErrorLogQueue() {
        return new Queue("admin.error.log.queue", true);
    }

    @Bean
    public Binding adminErrorLogBinding(Queue adminErrorLogQueue, TopicExchange logsExchange) {
        return BindingBuilder.bind(adminErrorLogQueue).to(logsExchange).with("#_Error");
    }

    @Bean
    public Queue adminPaymentFailedQueue() {
        return new Queue("admin.payment.failed.queue", true);
    }

    @Bean
    public Binding adminPaymentFailedBinding(Queue adminPaymentFailedQueue, DirectExchange paymentsExchange) {
        return BindingBuilder.bind(adminPaymentFailedQueue).to(paymentsExchange).with("payment.failed");
    }

    @Bean
    public DirectExchange paymentsExchange() {
        return new DirectExchange("payments", true, false);
    }

    // This queue is for the Seller Service to consume seller creation events
    @Bean
    public Queue sellerCreatedQueue() {
        return new Queue("seller.created.queue", true);
    }

    @Bean
    public Binding sellerCreatedBinding(Queue sellerCreatedQueue, TopicExchange adminExchange) {
        return BindingBuilder
                .bind(sellerCreatedQueue)
                .to(adminExchange)
                .with(ROUTING_CREATE_SELLER);
    }

    @Bean
    public TopicExchange logsExchange() {
        return ExchangeBuilder
                .topicExchange("logs")
                .durable(true)
                .build();
    }
}