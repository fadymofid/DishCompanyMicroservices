package com.example.seller.ejb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import jakarta.ejb.Stateless;

@Stateless
public class LogProducerBean {

    private static final String EXCHANGE_NAME = "log.exchange";
    private static final String EXCHANGE_TYPE = "topic";

    public void log(String serviceName, String severity, String message) {
        try (Connection connection = RabbitMQUtil.getConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, true);
            String routingKey = serviceName.toLowerCase() + "." + severity.toLowerCase();

            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            System.out.printf("Sent log message: [%s] %s%n", routingKey, message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
