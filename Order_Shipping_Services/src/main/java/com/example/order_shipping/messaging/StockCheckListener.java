package com.example.order_shipping.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.order_shipping.DTO.OrderRequest;

@Component
public class StockCheckListener {

    private static final Logger logger = LoggerFactory.getLogger(StockCheckListener.class);

    private final RabbitTemplate rabbitTemplate;

    public StockCheckListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "stock.check.queue")
    public void handleStockCheck(OrderRequest orderRequest) {
        try {
            // Simulate stock validation
            boolean stockAvailable = true; // Replace with actual logic

            if (stockAvailable) {
                rabbitTemplate.convertAndSend("order-exchange", "order.confirmed", orderRequest.getOrderId());
                logger.info("Stock validated for orderId: {}", orderRequest.getOrderId());
            } else {
                rabbitTemplate.convertAndSend("order-exchange", "order.rejected", "Insufficient stock");
                logger.warn("Stock insufficient for orderId: {}", orderRequest.getOrderId());
            }
        } catch (Exception e) {
            logger.error("Error handling stock check for orderId: {}", orderRequest.getOrderId(), e);
        }
    }
}