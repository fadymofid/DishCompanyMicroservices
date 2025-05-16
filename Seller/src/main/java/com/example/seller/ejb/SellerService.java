// SellerService.java (Remote Interface)
package com.example.seller.ejb;

import com.example.seller.models.Dish;
import com.example.seller.models.OrderItem;
import jakarta.ejb.Remote;

import java.sql.SQLException;
import java.util.List;

@Remote
public interface SellerService {
    void addOrderItem(OrderItem item);
    List<OrderItem> getOrderItemsByOrderId(Long orderId);
    void createDish(Dish dish, Long sellerId)throws SQLException;

}