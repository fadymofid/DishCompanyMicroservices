package com.example.order_shipping.DTO;

import java.math.BigDecimal;
import com.example.order_shipping.Models.OrderStatus;

public class OrderResponse {
    private Long orderId;
    private BigDecimal totalAmount;
    private OrderStatus status;

    public OrderResponse() {}

    public OrderResponse(Long orderId, BigDecimal totalAmount, OrderStatus status) {
        this.orderId     = orderId;
        this.totalAmount = totalAmount;
        this.status      = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}