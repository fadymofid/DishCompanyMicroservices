package com.example.seller.DAO;

import com.example.seller.models.Dish;
import com.example.seller.models.Seller;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishDAO {
    private final Connection conn;

    // Constructor accepts a Connection (like OrderDAL)
    public DishDAO(Connection conn) {
        this.conn = conn;
    }

    // Find all dishes by seller ID
    public List<Dish> findBySeller(Long sellerId) throws SQLException {
        String sql = "SELECT * FROM dish WHERE seller_id = ?";
        List<Dish> dishes = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Dish dish = mapResultSetToDish(rs);
                    dishes.add(dish);
                }
            }
        }
        return dishes;
    }

    // Save a new dish
    public void save(Dish dish) throws SQLException {
        String sql = "INSERT INTO dish (name, description, price, stock, seller_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getDescription());
            stmt.setBigDecimal(3, dish.getPrice());
            stmt.setInt(4, dish.getStock());
            stmt.setLong(5, dish.getSeller().getId()); // Assume Seller has ID

            stmt.executeUpdate();

            // Set generated dish ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    dish.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    // Update an existing dish
    public void update(Dish dish) throws SQLException {
        String sql = "UPDATE dish SET name = ?, description = ?, price = ?, stock = ?, seller_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getDescription());
            stmt.setBigDecimal(3, dish.getPrice());
            stmt.setInt(4, dish.getStock());
            stmt.setLong(5, dish.getSeller().getId());
            stmt.setLong(6, dish.getId());

            stmt.executeUpdate();
        }
    }

    // Find a dish by ID
    public Dish find(Long id) throws SQLException {
        String sql = "SELECT * FROM dish WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDish(rs);
                }
            }
        }
        return null;
    }

    // Helper method to map ResultSet to Dish object
    private Dish mapResultSetToDish(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getLong("id"));
        dish.setName(rs.getString("name"));
        dish.setDescription(rs.getString("description"));
        dish.setPrice(rs.getBigDecimal("price"));
        dish.setStock(rs.getInt("stock"));

        // Map seller_id to a Seller object with just the ID
        Seller seller = new Seller();
        seller.setId(rs.getLong("seller_id"));
        dish.setSeller(seller);

        return dish;
    }
}