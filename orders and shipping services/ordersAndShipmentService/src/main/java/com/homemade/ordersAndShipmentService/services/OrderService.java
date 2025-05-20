package com.homemade.ordersAndShipmentService.services;

import com.homemade.ordersAndShipmentService.dto.*;
import com.homemade.ordersAndShipmentService.entity.*;
import com.homemade.ordersAndShipmentService.repositories.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Value("${rabbitmq.stockCheckQueue:stock_check_queue}")
    private String stockCheckQueue;
    @Value("${rabbitmq.stockCheckResponseQueue:stock_check_response_queue}")
    private String stockCheckResponseQueue;
    @Value("${rabbitmq.paymentRequestQueue:payment_request_queue}")
    private String paymentRequestQueue;
    @Value("${rabbitmq.paymentResponseQueue:payment_response_queue}")
    private String paymentResponseQueue;
    @Value("${rabbitmq.customerNotificationQueue:customer_notification_queue}")
    private String customerNotificationQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private ObjectMapper objectMapper;

    public OrderResponse processOrder(OrderRequest req) {
        String correlationId = UUID.randomUUID().toString();

        // 1) Stock check
        StockCheckRequest scReq = new StockCheckRequest(correlationId, req.getItems());
        rabbitTemplate.convertAndSend(stockCheckQueue, scReq);

        StockCheckResponse scResp = receiveWithTimeout(stockCheckResponseQueue, correlationId, StockCheckResponse.class);
        if (scResp == null) {
            return new OrderResponse(null, OrderStatus.PENDING, "Stock service timeout");
        }
        if (!scResp.isAvailable()) {
            notifyCustomer(req.getCustomerId(), null, OrderStatus.CANCELLED, "Out of stock");
            return new OrderResponse(null, OrderStatus.CANCELLED, "Out of stock");
        }

        // 2) Create order in PENDING
        Order order = persistNewOrder(req);

        // 3) Payment
        PaymentRequest payReq = new PaymentRequest(correlationId, order.getId(), order.getTotalPrice());
        rabbitTemplate.convertAndSend(paymentRequestQueue, payReq);

        PaymentResponse payResp = receiveWithTimeout(paymentResponseQueue, correlationId, PaymentResponse.class);
        if (payResp == null || !payResp.isSuccess()) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepo.save(order);
            notifyCustomer(req.getCustomerId(), order.getId(), OrderStatus.CANCELLED,
                payResp == null ? "Payment timeout" : payResp.getFailureReason());
            return new OrderResponse(order.getId(), OrderStatus.CANCELLED,
                payResp == null ? "Payment timeout" : payResp.getFailureReason());
        }

        // 4) Confirm order
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepo.save(order);
        notifyCustomer(req.getCustomerId(), order.getId(), OrderStatus.CONFIRMED, "Order confirmed and processing.");
        return new OrderResponse(order.getId(), OrderStatus.CONFIRMED, "Order confirmed");
    }

    private <T> T receiveWithTimeout(String queue, String corrId, Class<T> clazz) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 5000) {
            Message msg = rabbitTemplate.receive(queue, 500);
            if (msg == null) continue;
            if (corrId.equals(msg.getMessageProperties().getCorrelationId())) {
                try {
                    return objectMapper.readValue(msg.getBody(), clazz);
                } catch (IOException e) { break; }
            }
        }
        return null;
    }

    private Order persistNewOrder(OrderRequest req) {
        List<OrderItem> items = req.getItems();
        double total = items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        Order o = new Order();
        o.setCustomerId(req.getCustomerId());
        o.setTotalPrice(total);
        o.setStatus(OrderStatus.PENDING);
        o.setCreatedAt(LocalDateTime.now());
        items.forEach(i -> i.setOrder(o));
        o.setItems(items);
        return orderRepo.save(o);
    }

    private void notifyCustomer(Long customerId, Long orderId, OrderStatus status, String message) {
        try {
            var notif = new java.util.HashMap<String, Object>();
            notif.put("customerId", customerId);
            notif.put("orderId", orderId);
            notif.put("status", status.name()); // Send enum as string
            notif.put("message", message);
            rabbitTemplate.convertAndSend(customerNotificationQueue, objectMapper.writeValueAsString(notif));
        } catch (Exception e) {
            // Log error
        }
    }

    public void notifyCustomerPublic(Long customerId, Long orderId, OrderStatus status, String message) {
        notifyCustomer(customerId, orderId, status, message);
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepo.findByCustomerId(customerId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepo.findById(orderId).orElse(null);
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepo.save(order);
        }
    }

    public void confirmOrder(Long orderId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepo.save(order);
        }
    }

    public void markDelivered(Long orderId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepo.save(order);
        }
    }
}

