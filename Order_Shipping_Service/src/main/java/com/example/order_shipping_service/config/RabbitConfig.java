package com.example.order_shipping_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String ORDER_EXCHANGE        = "order-exchange";
    public static final String STOCK_CHECK_QUEUE     = "stock.check.queue";
    public static final String PAYMENT_PROCESS_QUEUE = "payment.process.queue";
    public static final String ORDER_CONF_QUEUE      = "order.confirmed.queue";
    public static final String ORDER_REJ_QUEUE       = "order.rejected.queue";
    public static final String ROUTING_ORDER_CONFIRMED  = "order.confirmed";

    public static final String CUSTOMER_EXCHANGE         = "customer-exchange";
    public static final String CUSTOMER_REQUEST_QUEUE    = "customer.request.queue";
    public static final String CUSTOMER_RESPONSE_QUEUE   = "customer.response.queue";
    public static final String ROUTING_CUSTOMER_REQUEST = "customer.request";

    public static final String DISH_EXCHANGE             = "dish-exchange";
    public static final String DISH_REQUEST_QUEUE        = "dish.request.queue";
    public static final String DISH_RESPONSE_QUEUE       = "dish.response.queue";
    public static final String ROUTING_DISH_REQUEST = "dish.request";

    // Exchanges
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange("order-exchange", true, false);
    }

    @Bean
    public TopicExchange customerExchange() {
        return new TopicExchange("customer-exchange", true, false);
    }

    @Bean
    public TopicExchange dishExchange() {
        return new TopicExchange("dish-exchange", true, false);
    }

    // Queues
    @Bean
    public Queue stockCheckQueue() {
        return QueueBuilder.durable("stock.check.queue").build();
    }

    @Bean
    public Queue paymentProcessQueue() {
        return QueueBuilder.durable("payment.process.queue").build();
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return QueueBuilder.durable("order.confirmed.queue").build();
    }

    @Bean
    public Queue orderRejectedQueue() {
        return QueueBuilder.durable("order.rejected.queue").build();
    }

    @Bean
    public Queue dishRequestQueue() {
        return QueueBuilder.durable("dish.request.queue").build();
    }

    @Bean
    public Queue dishResponseQueue() {
        return QueueBuilder.durable("dish.response.queue").build();
    }

    @Bean
    public Queue customerRequestQueue() {
        return QueueBuilder.durable("customer.request.queue").build();
    }

    @Bean
    public Queue customerResponseQueue() {
        return QueueBuilder.durable("customer.response.queue").build();
    }

    @Bean
    public Queue inventoryReleaseQueue() {
        return QueueBuilder.durable("inventory.release.queue").build();
    }

    // Bindings
    @Bean
    public Binding bindStockCheck() {
        return BindingBuilder.bind(stockCheckQueue())
                .to(orderExchange())
                .with("stock.check");
    }

    @Bean
    public Binding bindPaymentProcess() {
        return BindingBuilder.bind(paymentProcessQueue())
                .to(orderExchange())
                .with("payment.process");
    }

    @Bean
    public Binding bindOrderConfirmed() {
        return BindingBuilder.bind(orderConfirmedQueue())
                .to(orderExchange())
                .with("order.confirmed");
    }

    @Bean
    public Binding bindOrderRejected() {
        return BindingBuilder.bind(orderRejectedQueue())
                .to(orderExchange())
                .with("order.rejected");
    }

    @Bean
    public Binding bindDishRequest() {
        return BindingBuilder.bind(dishRequestQueue())
                .to(dishExchange())
                .with("dish.request");
    }

    @Bean
    public Binding bindDishResponse() {
        return BindingBuilder.bind(dishResponseQueue())
                .to(dishExchange())
                .with("dish.response");
    }

    @Bean
    public Binding bindCustomerRequest() {
        return BindingBuilder.bind(customerRequestQueue())
                .to(customerExchange())
                .with("customer.request");
    }

    @Bean
    public Binding bindCustomerResponse() {
        return BindingBuilder.bind(customerResponseQueue())
                .to(customerExchange())
                .with("customer.response");
    }

    @Bean
    public Binding bindInventoryRelease() {
        return BindingBuilder.bind(inventoryReleaseQueue())
                .to(orderExchange())
                .with("inventory.release");
    }
}
