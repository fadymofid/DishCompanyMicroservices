package com.example.order_shipping.service;

import com.example.order_shipping.DTO.OrderRequest;
import com.example.order_shipping.DTO.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse processOrder(OrderRequest request);
    List<OrderResponse> getOrdersByCustomer(Long customerId);
    void cancelOrder(String orderId);
}