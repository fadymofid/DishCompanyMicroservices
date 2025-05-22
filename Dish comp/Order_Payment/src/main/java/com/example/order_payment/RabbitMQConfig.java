package com.example.order_payment;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // your existing queue beans
    @Bean public Queue stockCheckQueue()              { return new Queue("stock_check_queue", false); }
    @Bean public Queue stockCheckResponseQueue()      { return new Queue("stock_check_response_queue", false); }
    @Bean public Queue paymentRequestQueue()          { return new Queue("payment_request_queue", false); }
    @Bean public Queue paymentResponseQueue()         { return new Queue("payment_response_queue", false); }
    @Bean public Queue customerNotificationQueue()    { return new Queue("customer_notification_queue", false); }

    // 1) Define a Jackson-based JSON converter
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 2) Override the RabbitTemplate so it uses the JSON converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter);
        return template;
    }
}
