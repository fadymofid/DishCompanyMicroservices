package com.example.dishescompany.Repo;

import com.example.dishescompany.Models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for order line items.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // find all items for a given order
    List<OrderItem> findByOrderId(Long orderId);
}
