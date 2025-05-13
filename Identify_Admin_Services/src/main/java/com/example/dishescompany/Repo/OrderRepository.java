package com.example.dishescompany.Repo;

import com.example.dishescompany.Models.Order;
import com.example.dishescompany.Models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for customer orders.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // find all orders by customer
    List<Order> findByCustomerId(Long customerId);

    // find by status
    List<Order> findByStatus(OrderStatus status);
}
