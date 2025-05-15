package com.example.order_shipping_service.Listeners;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void log(String serviceName, String severity, String message) {
        String routingKey = serviceName + "." + severity;
        rabbitTemplate.convertAndSend("log.exchange", routingKey, message);
    }
}