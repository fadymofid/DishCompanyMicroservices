package com.example.seller.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.seller.models.OrderItem;

public class OrderItemDAO {
    private final Connection conn;

    public OrderItemDAO(Connection conn) {
        this.conn = conn;
    }

    // Save an order item
    public void save(OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_item (order_id, dish_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, item.getOrder_id());
            stmt.setLong(2, item.getDish().getId());
            stmt.setInt(3, item.getQuantity());
            stmt.executeUpdate();

            // Set generated ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    // Find order items by order ID
    public List<OrderItem> findByOrderId(Long orderId) throws SQLException {
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        List<OrderItem> items = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = mapResultSetToOrderItem(rs);
                    items.add(item);
                }
            }
        }
        return items;
    }

    // Helper method to map ResultSet to OrderItem
    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getLong("id"));
        // Assume Order and Dish have setters for IDs
        item.setOrder_id((int) rs.getLong("order_id"));
        item.getDish().setId(rs.getLong("dish_id"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    }
}