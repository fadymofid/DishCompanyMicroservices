package com.example.order_shipping.controller;

import com.example.order_shipping.DTO.OrderRequest;
import com.example.order_shipping.DTO.OrderResponse;
import com.example.order_shipping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Place a new order. Returns a single OrderResponse
     * containing orderId, totalAmount, and status.
     */
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        OrderResponse response = orderService.processOrder(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all orders for a given customer.
     * Returns a list of OrderResponse objects.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(
            @PathVariable Long customerId) {

        List<OrderResponse> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }
}
