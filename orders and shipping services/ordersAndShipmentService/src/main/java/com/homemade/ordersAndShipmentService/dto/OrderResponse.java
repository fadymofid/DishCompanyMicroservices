package com.homemade.ordersAndShipmentService.dto;

import com.homemade.ordersAndShipmentService.entity.OrderStatus;

public class OrderResponse {
    private Long orderId;
    private OrderStatus status;
    private String message;

    public OrderResponse(Long orderId, OrderStatus status, String message) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    public Long getOrderId() { return orderId; }
    public OrderStatus getStatus() { return status; }
    public String getMessage() { return message; }
}