package com.example.order_shipping_service.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_shipping_service.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Process a payment for an order.
     * @param orderId The ID of the order.
     * @param orderSubtotal The subtotal of the order.
     * @param shippingFee The shipping fee for the order.
     * @return A success or failure message.
     */
    @PostMapping("/process")
    public ResponseEntity<String> processPayment(
            @RequestParam Long orderId,
            @RequestParam BigDecimal orderSubtotal,
            @RequestParam BigDecimal shippingFee) {
        try {
            paymentService.processPayment(orderId, orderSubtotal, shippingFee);
            return ResponseEntity.ok("Payment processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Payment processing failed: " + e.getMessage());
        }
    }

    /**
     * Handle payment failure for an order.
     * @param orderId The ID of the order.
     * @return A failure message.
     */
    @PostMapping("/failure")
    public ResponseEntity<String> handlePaymentFailure(@RequestParam Long orderId) {
        paymentService.handlePaymentFailure(orderId);
        return ResponseEntity.ok("Payment failure handled for order: " + orderId);
    }
}