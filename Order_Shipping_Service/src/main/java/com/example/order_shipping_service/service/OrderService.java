package com.example.order_shipping_service.service;

import com.example.order_shipping_service.DTO.OrderRequest;
import com.example.order_shipping_service.DTO.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse processOrder(OrderRequest request);
    List<OrderResponse> getOrdersByCustomer(Long customerId);
    void cancelOrder(String orderId);
    OrderResponse getOrderDetails(String orderId);
}