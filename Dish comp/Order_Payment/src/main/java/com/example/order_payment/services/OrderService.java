package com.example.order_payment.services;

import com.example.order_payment.dto.*;
import com.example.order_payment.entity.Order;
import com.example.order_payment.entity.OrderItem;
import com.example.order_payment.entity.OrderStatus;
import com.example.order_payment.repositories.OrderItemRepository;
import com.example.order_payment.repositories.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private OrderItemRepository orderItemRepo;
    @Autowired
    private ObjectMapper objectMapper;


    public OrderResponse processOrder(OrderRequest req) {
        String correlationId = UUID.randomUUID().toString();
        System.out.println(">>> Starting order processing with correlationId: " + correlationId);

        try {
            // 1) Validate minimum charge
            double total = req.getItems().stream()
                    .mapToDouble(i -> i.getPrice() * i.getQuantity())
                    .sum();
            if (total < 20) {
                System.out.println(">>> Order total below $20. Cancelling.");
                notifyCustomer(req.getCustomerId(), null, OrderStatus.CANCELLED, "Minimum order is $20");
                return new OrderResponse(null, OrderStatus.CANCELLED, "Minimum order is $20");
            }

            // 2) Stock check
            StockCheckRequest scReq = new StockCheckRequest(correlationId, req.getItems());
            MessageProperties props = MessagePropertiesBuilder.newInstance()
                    .setCorrelationId(correlationId)
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();
            Message stockMsg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(scReq)).andProperties(props).build();
            rabbitTemplate.send(stockCheckQueue, stockMsg);
            System.out.println(">>> Sent stock check request");

            StockCheckResponse scResp = receiveWithTimeout(stockCheckResponseQueue, correlationId, StockCheckResponse.class);
            if (scResp == null || !scResp.isAvailable()) {
                String reason = (scResp == null) ? "Stock check timeout" : "Items out of stock";
                System.out.println(">>> Stock check failed: " + reason);
                notifyCustomer(req.getCustomerId(), null, OrderStatus.CANCELLED, reason);
                return new OrderResponse(null, OrderStatus.CANCELLED, reason);
            }

            // 3) Payment processing
            PaymentRequest payReq = new PaymentRequest(
                    correlationId, req.getCustomerId(), null, total
            );
            Message payMsg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(payReq)).andProperties(props).build();
            rabbitTemplate.send(paymentRequestQueue, payMsg);
            System.out.println(">>> Sent payment request");

            PaymentResponse payResp = receiveWithTimeout(paymentResponseQueue, correlationId, PaymentResponse.class);
            if (payResp == null || !payResp.isSuccess()) {
                String reason = (payResp == null) ? "Payment timeout" : payResp.getFailureReason();
                System.out.println(">>> Payment failed: " + reason);
                notifyCustomer(req.getCustomerId(), null, OrderStatus.CANCELLED, reason);
                return new OrderResponse(null, OrderStatus.CANCELLED, reason);
            }

            // 4) Persist confirmed order
            Order order = persistNewOrder(req, total);
            System.out.println(">>> Order persisted and confirmed: " + order.getId());

            // 5) Stock update to seller
            StockUpdateRequest updateRequest = new StockUpdateRequest();
            updateRequest.setOrderId(order.getId());
            updateRequest.setItems(order.getItems().stream().map(item -> {
                DishQuantity dq = new DishQuantity();
                dq.setDishId(item.getDishId());
                dq.setQuantity(item.getQuantity());
                return dq;
            }).collect(Collectors.toList()));
            Message stockUpdateMsg = MessageBuilder
                    .withBody(objectMapper.writeValueAsBytes(updateRequest))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();
            rabbitTemplate.send("stock_update_queue", stockUpdateMsg);
            System.out.println(">>> Sent stock update to seller");

            // 6) Notify customer
            notifyCustomer(req.getCustomerId(), order.getId(), OrderStatus.CONFIRMED, "Order confirmed and processing");
            return new OrderResponse(order.getId(), OrderStatus.CONFIRMED, "Order confirmed");

        } catch (Exception e) {
            System.err.println(">>> Error processing order: " + e.getMessage());
            e.printStackTrace();
            notifyCustomer(req.getCustomerId(), null, OrderStatus.CANCELLED, "Internal error processing order");
            return new OrderResponse(null, OrderStatus.CANCELLED, "Internal error");
        }
    }
    private <T> T receiveWithTimeout(String queue, String corrId, Class<T> clazz) {
        long start = System.currentTimeMillis();
        long timeout = 10000; // 10 second timeout

        while (System.currentTimeMillis() - start < timeout) {
            Message msg = rabbitTemplate.receive(queue, 500);
            if (msg != null) {
                String msgCorrId = msg.getMessageProperties().getCorrelationId();
                System.out.println(">>> Received message with correlationId: " + msgCorrId);
                
                if (corrId.equals(msgCorrId)) {
                    try {
                        return objectMapper.readValue(msg.getBody(), clazz);
                    } catch (IOException e) {
                        System.err.println(">>> Error deserializing response: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(">>> Correlation ID mismatch: expected=" + corrId + ", got=" + msgCorrId);
                }
            }
        }
        System.out.println(">>> Timeout waiting for response");
        return null;
    }

    private Order persistNewOrder(OrderRequest req, double total) {
        Order order = new Order();
        order.setCustomerId(req.getCustomerId());
        order.setTotalPrice(total);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setCreatedAt(LocalDateTime.now());
        order.setDeliveryAddress(req.getAddress());

        List<OrderItem> items = req.getItems();
        items.forEach(item -> item.setOrder(order));
        order.setItems(items);

        return orderRepo.save(order);
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
    public void handleStockCheckResult(StockCheckResponse response) {
        String correlationId = response.getCorrelationId();
        boolean available = response.isAvailable();

        System.out.println("Handling stock check result for correlationId: " + correlationId);

        if (available) {
            // Save order to DB using saved request (you need to store pending orders using the correlationId)
            System.out.println("Stock available. Proceeding to save order...");
        } else {
            // Handle failed order (notify customer, etc.)
            System.out.println("Stock unavailable. Cancelling order.");
        }
    }

    public String getShippingAddress(int orderId) {
        Order order = orderRepo.findById(orderId);
        if (order != null) {
            return order.getDeliveryAddress();
        }
        return null;

    }
    public List<Long> getOrderIdsByDishId(Long dishId) {
        return orderItemRepo.findByDishId(dishId).stream()
                .map(item -> item.getOrder().getId())
                .distinct()
                .collect(Collectors.toList());
    }
    public Long getCustomerIdByOrderId(Long orderId) {
        return orderRepo.findById(orderId)
                .map(Order::getCustomerId)
                .orElse(null);
    }
}

