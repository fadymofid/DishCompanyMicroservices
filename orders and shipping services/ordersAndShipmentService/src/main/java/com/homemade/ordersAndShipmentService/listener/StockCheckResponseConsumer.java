package com.homemade.ordersAndShipmentService.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homemade.ordersAndShipmentService.dto.StockCheckResponse;
import com.homemade.ordersAndShipmentService.services.OrderService;
import com.rabbitmq.client.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;

@Component
public class StockCheckResponseConsumer {

    private static final String STOCK_CHECK_RESPONSE_QUEUE = "stock_check_response_queue";

    @Autowired
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(STOCK_CHECK_RESPONSE_QUEUE, false, false, false, null);
        System.out.println(" [✓] Waiting for responses in '" + STOCK_CHECK_RESPONSE_QUEUE + "'...");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [✓] Received StockCheckResponse: " + message);
            try {
                StockCheckResponse response = objectMapper.readValue(message, StockCheckResponse.class);
                orderService.handleStockCheckResult(response);  // ✅ Your logic to save/cancel order
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume(STOCK_CHECK_RESPONSE_QUEUE, true, deliverCallback, consumerTag -> {});
    }
}
