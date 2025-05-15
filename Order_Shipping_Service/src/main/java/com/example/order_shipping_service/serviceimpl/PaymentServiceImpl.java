package com.example.order_shipping_service.serviceimpl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.order_shipping_service.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final BigDecimal MINIMUM_CHARGE = new BigDecimal("20.00");

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public PaymentServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Process payment for an order.
     * - Publishes to 'payments' exchange with routing keys 'payment.success' or 'payment.failed'.
     * - Logs to 'logs' topic exchange.
     * - Notifies customer on 'customer.notifications'.
     */
    @Override
    @Transactional
    public void processPayment(Long orderId,
                               BigDecimal orderSubtotal,
                               BigDecimal shippingFee) {
        BigDecimal totalCharge = orderSubtotal.add(shippingFee);

        // 1) Below minimum → immediate failure (and rollback)
        if (totalCharge.compareTo(MINIMUM_CHARGE) < 0) {
            String reason = "BelowMinimum";
            publishPaymentFailed(orderId, reason);
            throw new IllegalStateException("Order " + orderId
                    + " charge " + totalCharge + " below minimum " + MINIMUM_CHARGE);
        }

        // 2) Attempt external payment
        boolean success = attemptPaymentProcessing(totalCharge);
        if (success) {
            publishPaymentSuccess(orderId, totalCharge);
        } else {
            String reason = "GatewayDecline";
            publishPaymentFailed(orderId, reason);
            throw new IllegalStateException("Payment gateway declined for order " + orderId);
        }
    }

    public boolean attemptPaymentProcessing(BigDecimal amount) {
        // TODO: integrate real gateway here
        return true;
    }

    public void publishPaymentSuccess(Long orderId, BigDecimal amount) {
        // 1) payments direct exchange
        Map<String,Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("amount", amount);
        rabbitTemplate.convertAndSend("payments", "payment.success", payload);

        // 2) logs topic exchange
        String logMsg = "Payment of " + amount + " succeeded for order " + orderId;
        rabbitTemplate.convertAndSend("logs", "Payment_Info",
                createLogPayload(orderId, logMsg));

        // 3) customer notification
        rabbitTemplate.convertAndSend("customer.notifications", String.valueOf(orderId),
                "Your payment of " + amount + " was successful.");
    }

    public void publishPaymentFailed(Long orderId, String reason) {
        // 1) payments direct exchange
        Map<String,Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("reason", reason);
        rabbitTemplate.convertAndSend("payments", "payment.failed", payload);

        // 2) logs topic exchange
        String logMsg = "Payment failed for order " + orderId + " (reason: " + reason + ")";
        rabbitTemplate.convertAndSend("logs", "Payment_Error",
                createLogPayload(orderId, logMsg));

        // 3) customer notification
        rabbitTemplate.convertAndSend("customer.notifications", String.valueOf(orderId),
                "We’re sorry—your payment failed: " + reason);
    }

    public Map<String,Object> createLogPayload(Long orderId, String message) {
        Map<String,Object> log = new HashMap<>();
        log.put("orderId", orderId);
        log.put("service", "PaymentService");
        log.put("message", message);
        log.put("timestamp", System.currentTimeMillis());
        return log;
    }
    @Override
    public void handlePaymentFailure(Long orderId) {
        String message = "Payment failed for order: " + orderId;
        rabbitTemplate.convertAndSend("payment.failure.exchange", "PaymentFailed", message);
    }
}
