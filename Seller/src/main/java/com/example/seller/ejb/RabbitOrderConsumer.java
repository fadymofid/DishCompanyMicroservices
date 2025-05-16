package com.example.seller.ejb;

import com.example.seller.ejb.RabbitMQUtil;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Startup
@Singleton
public class RabbitOrderConsumer {

    private static final String QUEUE_NAME = "order.rejected.queue";

    @PostConstruct
    public void listen() {
        new Thread(() -> {
            try {
                Connection connection = RabbitMQUtil.getConnection();
                Channel channel = connection.createChannel();

                channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody());
                    System.out.println("RabbitMQ Consumer: " + message);

                    // handle rejected order logic...
                };

                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
