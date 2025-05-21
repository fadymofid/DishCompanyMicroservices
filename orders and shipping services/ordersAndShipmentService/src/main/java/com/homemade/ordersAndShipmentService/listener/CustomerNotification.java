package com.homemade.ordersAndShipmentService.listener;

import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import com.homemade.ordersAndShipmentService.entity.OrderStatus;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class CustomerNotification {

    private static final String CUSTOMER_QUEUE = "customer_notification_queue";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(CUSTOMER_QUEUE, false, false, false, null);
            System.out.println("notifications in '" + CUSTOMER_QUEUE + "' queue");
            System.out.println("Waiting for messages...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received: " + message);

                try {
                    JSONObject notification = new JSONObject(message);
                    Long customerId = notification.getLong("customerId");
                    OrderStatus status = OrderStatus.valueOf(notification.getString("status"));
                    String notifMessage = notification.getString("message");

                    System.out.println(customerId + ": " + notifMessage + " (Status: " + status + ")");
                } catch (Exception e) {
                    System.err.println("Erorr: Failed to process: " + e.getMessage());
                }
            };

            channel.basicConsume(CUSTOMER_QUEUE, true, deliverCallback, consumerTag -> {});

            while (true) {
                Thread.sleep(3000);
            }
        }
    }
}
