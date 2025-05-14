package com.example.order_shipping.controller;

import com.example.order_shipping.DTO.OrderRequest;
import com.example.order_shipping.DTO.OrderResponse;
import com.example.order_shipping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Create a new order with multiple dishes and varied amounts.
     * @param orderRequest The order request containing customer, dishes, and shipping details.
     * @return The response with order details.
     */
    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.processOrder(orderRequest);
    }

    /**
     * Get details of a specific order by its ID.
     * @param orderId The ID of the order.
     * @return The response with order details.
     */
    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable String orderId) {
        return orderService.getOrderDetails(orderId);
    }

    /**
     * Get all current and past orders for a specific customer.
     * @param customerId The ID of the customer.
     * @return A list of order responses.
     */
    @GetMapping("/customer/{customerId}")
    public List<OrderResponse> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }

    /**
     * Cancel an order by its ID.
     * @param orderId The ID of the order to cancel.
     */
    @DeleteMapping("/{orderId}")
    public void cancelOrder(@PathVariable String orderId) {
        orderService.cancelOrder(orderId);
    }
}