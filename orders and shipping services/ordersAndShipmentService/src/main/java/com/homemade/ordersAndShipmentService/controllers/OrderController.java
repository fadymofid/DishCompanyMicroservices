package com.homemade.ordersAndShipmentService.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.homemade.ordersAndShipmentService.dto.OrderRequest;
import com.homemade.ordersAndShipmentService.dto.OrderResponse;
import com.homemade.ordersAndShipmentService.entity.Order;
import com.homemade.ordersAndShipmentService.dto.Notification;
import com.homemade.ordersAndShipmentService.services.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest req) {
        OrderResponse resp = orderService.processOrder(req);

        switch (resp.getStatus()) {
            case CONFIRMED:
                return ResponseEntity.ok(resp);
            case PENDING:
                return ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body(resp);
            case CANCELLED:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)   // or BAD_REQUEST
                        .body(resp);
            default:
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(resp);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getCustomerOrders(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm/{orderId}")
    public ResponseEntity<String> confirmOrder(@PathVariable Long orderId) {
        orderService.confirmOrder(orderId);
        return ResponseEntity.ok("Order confirmed.");
    }

    @PostMapping("/deliver/{orderId}")
    public ResponseEntity<String> markDelivered(@PathVariable Long orderId) {
        orderService.markDelivered(orderId);
        return ResponseEntity.ok("Order marked as delivered.");
    }

    @GetMapping("/notifications/{customerId}")
    public ResponseEntity<List<Map<String, Object>>> getCustomerNotifications(@PathVariable Long customerId) {
        List<Map<String, Object>> notifications = Notification.getAndClearNotificationsForCustomer(customerId);
        return ResponseEntity.ok(notifications);
    }
}

