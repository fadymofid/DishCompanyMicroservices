package com.example.order_shipping.Repo;

import com.example.order_shipping.Models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // You can add custom finder methods if needed, e.g.:
    // List<Order> findByCustomerId(Long customerId);
    // List<Order> findByStatus(OrderStatus status);
}
