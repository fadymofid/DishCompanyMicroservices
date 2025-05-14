package com.example.order_shipping.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);

    @RabbitListener(queues = "order.confirmed.queue")
    public void handleOrderConfirmed(String orderId) {
        try {
            // Notify customer about order confirmation
            logger.info("Order confirmed: {}", orderId);
            // Additional logic for notifying the customer can be added here
        } catch (Exception e) {
            logger.error("Error handling order confirmation for orderId: {}", orderId, e);
        }
    }

    @RabbitListener(queues = "order.rejected.queue")
    public void handleOrderRejected(String reason) {
        try {
            // Notify customer about order rejection
            logger.info("Order rejected: {}", reason);
            // Additional logic for notifying the customer can be added here
        } catch (Exception e) {
            logger.error("Error handling order rejection: {}", reason, e);
        }
    }
}