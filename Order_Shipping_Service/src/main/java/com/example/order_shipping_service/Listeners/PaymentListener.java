package com.example.order_shipping_service.Listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.order_shipping_service.Models.Order;
import com.example.order_shipping_service.Models.OrderStatus;
import com.example.order_shipping_service.Repo.OrderRepository;

@Component
public class PaymentListener {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public PaymentListener(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "payments.success")
    public void handlePaymentSuccess(String orderId) {
        Order order = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        // Notify customer about order confirmation
        rabbitTemplate.convertAndSend("order-exchange", "order.confirmed", orderId);
    }

    @RabbitListener(queues = "payments.failed")
    public void handlePaymentFailed(String orderId) {
        Order order = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Trigger compensating actions
        rabbitTemplate.convertAndSend("inventory-exchange", "inventory.release", orderId);
        rabbitTemplate.convertAndSend("shipping-exchange", "shipping.release", orderId);

        // Notify customer about order cancellation
        rabbitTemplate.convertAndSend("order-exchange", "order.rejected", orderId);
    }
}