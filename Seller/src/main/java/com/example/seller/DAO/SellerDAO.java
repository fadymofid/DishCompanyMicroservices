package com.example.seller.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.seller.models.Seller;

public class SellerDAO {
    private final Connection conn;

    public SellerDAO(Connection conn) {
        this.conn = conn;
    }

    // Find seller by ID
    public Seller find(Long id) throws SQLException {
        String sql = "SELECT * FROM seller WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSeller(rs);
                }
            }
        }
        return null;
    }

    // Find seller by username
    public Seller findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM seller WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSeller(rs);
                }
            }
        }
        return null;
    }

    // Save a new seller
    public void save(Seller seller) throws SQLException {
        String sql = "INSERT INTO seller (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, seller.getUsername());
            stmt.setString(2, seller.getPassword());
            stmt.executeUpdate();

            // Set generated ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    seller.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    // Helper method to map ResultSet to Seller
    private Seller mapResultSetToSeller(ResultSet rs) throws SQLException {
        Seller seller = new Seller();
        seller.setId(rs.getLong("id"));
        seller.setUsername(rs.getString("username"));
        seller.setPassword(rs.getString("password"));
        return seller;
    }
}