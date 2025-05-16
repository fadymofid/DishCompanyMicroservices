package com.example.dishescompany.Lisstener;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailureListener {

    @RabbitListener(queues = "admin.payment.failed.queue")
    public void handlePaymentFailure(String message) {
        System.out.println("[ADMIN] Payment Failure Received: " + message);
    }
    @RabbitListener(queues = "admin.payment.failed.queue")
    public void handlePaymentFailed(Map<String, Object> payload) {
    // Notify admin about payment failure
}
}