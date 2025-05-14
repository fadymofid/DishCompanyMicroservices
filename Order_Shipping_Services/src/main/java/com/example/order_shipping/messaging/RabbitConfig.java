package com.example.order_shipping.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Exchanges
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange("order-exchange");
    }

    @Bean
    public TopicExchange dishExchange() {
        return new TopicExchange("dish-exchange");
    }

    @Bean
    public TopicExchange customerExchange() {
        return new TopicExchange("customer-exchange");
    }

    // Queues
    @Bean
    public Queue stockCheckQueue() {
        return new Queue("stock.check.queue");
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return new Queue("order.confirmed.queue");
    }

    @Bean
    public Queue orderRejectedQueue() {
        return new Queue("order.rejected.queue");
    }

    @Bean
    public Queue paymentQueue() {
        return new Queue("payment.process.queue");
    }

    @Bean
    public Queue dishRequestQueue() {
        return new Queue("dish.request.queue");
    }

    @Bean
    public Queue dishResponseQueue() {
        return new Queue("dish.response.queue");
    }

    @Bean
    public Queue customerRequestQueue() {
        return new Queue("customer.request.queue");
    }

    @Bean
    public Queue customerResponseQueue() {
        return new Queue("customer.response.queue");
    }

    // Bindings
    @Bean
    public Binding stockCheckBinding(Queue stockCheckQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(stockCheckQueue).to(orderExchange).with("stock.check");
    }

    @Bean
    public Binding orderConfirmedBinding(Queue orderConfirmedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderConfirmedQueue).to(orderExchange).with("order.confirmed");
    }

    @Bean
    public Binding orderRejectedBinding(Queue orderRejectedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderRejectedQueue).to(orderExchange).with("order.rejected");
    }

    @Bean
    public Binding paymentBinding(Queue paymentQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(paymentQueue).to(orderExchange).with("payment.process");
    }

    @Bean
    public Binding dishRequestBinding(Queue dishRequestQueue, TopicExchange dishExchange) {
        return BindingBuilder.bind(dishRequestQueue).to(dishExchange).with("dish.request");
    }

    @Bean
    public Binding dishResponseBinding(Queue dishResponseQueue, TopicExchange dishExchange) {
        return BindingBuilder.bind(dishResponseQueue).to(dishExchange).with("dish.response");
    }

    @Bean
    public Binding customerRequestBinding(Queue customerRequestQueue, TopicExchange customerExchange) {
        return BindingBuilder.bind(customerRequestQueue).to(customerExchange).with("customer.request");
    }

    @Bean
    public Binding customerResponseBinding(Queue customerResponseQueue, TopicExchange customerExchange) {
        return BindingBuilder.bind(customerResponseQueue).to(customerExchange).with("customer.response");
    }
}