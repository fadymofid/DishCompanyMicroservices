// SellerService.java (Remote Interface)
package com.ejb;

import com.models.OrderItem;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface SellerService {
    void addOrderItem(OrderItem item);
    List<OrderItem> getOrderItemsByOrderId(Long orderId);
}