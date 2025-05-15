// SellerServiceBean.java (Stateless EJB)
package com.ejb;

import com.DAO.OrderItemDAO;
import com.models.OrderItem;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class SellerServiceBean implements SellerService {

    @EJB
    private OrderItemDAO orderItemDAO;

    @Override
    public void addOrderItem(OrderItem item) {
        orderItemDAO.save(item);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemDAO.findByOrderId(orderId);
    }
}
