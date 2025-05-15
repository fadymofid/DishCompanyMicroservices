package com.example.order_shipping_service.service;

import java.math.BigDecimal;

public interface PaymentService {
    /**
     * Process payment for the given order.
     * @param orderId the ID of the order to process.
     * @param orderSubtotal the subtotal amount of the order.
     * @param shippingFee the shipping fee to include.
     * @throws IllegalStateException if payment cannot be completed (e.g. below minimum or gateway failure).
     */
    void processPayment(Long orderId, BigDecimal orderSubtotal, BigDecimal shippingFee);
}
