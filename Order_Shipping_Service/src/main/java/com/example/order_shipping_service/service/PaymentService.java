package com.example.order_shipping_service.service;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {
    void processPayment(Long orderId,
                               BigDecimal orderSubtotal,
                               BigDecimal shippingFee);
    void handlePaymentFailure(Long orderId);
    void publishPaymentFailed(Long orderId, String reason);
    Map<String,Object> createLogPayload(Long orderId, String message);
    void publishPaymentSuccess(Long orderId, BigDecimal amount);
    boolean attemptPaymentProcessing(BigDecimal amount);
}
