// SellerServiceBean.java (Stateless EJB)
package com.example.seller.ejb;

import java.sql.SQLException;
import java.util.List;

import com.example.seller.DAO.DishDAO;
import com.example.seller.DAO.OrderItemDAO;
import com.example.seller.DAO.SellerDAO;
import com.example.seller.models.Dish;
import com.example.seller.models.OrderItem;
import com.example.seller.models.Seller;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class SellerServiceBean implements SellerService {

    @EJB
    private OrderItemDAO orderItemDAO;
    @EJB
    private DishDAO dishDAO;

    @EJB
    private SellerDAO sellerDAO;
    @Override
    public void addOrderItem(OrderItem item) {
        try {
            orderItemDAO.save(item);
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order item", e);
        }
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        try {
            return orderItemDAO.findByOrderId(orderId);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving order items by order ID", e);
        }
    }

    @Override
    public void createDish(Dish dish, Long sellerId) throws SQLException {
        Seller seller = sellerDAO.find(sellerId);
        dish.setSeller(seller);
        dishDAO.save(dish);
    }
}
