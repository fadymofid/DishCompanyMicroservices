package service2.DAL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import service2.Model.Dish;

public class DishDAL {
    private Connection conn;

    public DishDAL(Connection conn) {
        this.conn = conn;
    }




    public List<Dish> getAllDishesBySeller(int sellerId) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM dishes WHERE seller_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sellerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Dish dish = new Dish();
                dish.setDishId(rs.getInt("dish_id"));
                dish.setSellerId(rs.getInt("seller_id"));
                dish.setDishName(rs.getString("dish_name"));
                dish.setDescription(rs.getString("description"));
                dish.setPrice(rs.getDouble("price"));
                dish.setStockQuantity(rs.getInt("stock_quantity"));
                dishes.add(dish);
            }
        }
        return dishes;
    }
    public Dish GetDishById(int Id) throws SQLException{
        String sql = "SELECT * FROM dishes WHERE dish_id = ?";
        Dish dish = new Dish();
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();   
            if (rs.next()) {
                dish.setDishId(rs.getInt("dish_id"));
                dish.setSellerId(rs.getInt("seller_id"));
                dish.setDishName(rs.getString("dish_name"));
                dish.setDescription(rs.getString("description"));
                dish.setPrice(rs.getDouble("price"));
                dish.setStockQuantity(rs.getInt("stock_quantity"));
            }
        }
        return dish;
    }

    public List<Dish> getAllDishes() throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM dishes";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Dish dish = new Dish();
                dish.setDishId(rs.getInt("dish_id"));
                dish.setSellerId(rs.getInt("seller_id"));
                dish.setDishName(rs.getString("dish_name"));
                dish.setDescription(rs.getString("description"));
                dish.setPrice(rs.getDouble("price"));
                dish.setStockQuantity(rs.getInt("stock_quantity"));
                dishes.add(dish);
            }
        }
        return dishes;
    }

    public void deductStock(int dishId, int quantity) throws SQLException {
        String sql = "UPDATE dishes SET stock_quantity = stock_quantity - ? WHERE dish_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, dishId);
            stmt.executeUpdate();
        }
    }

     /** Create a new dish. */
    public void addDish(Dish dish) throws SQLException {
        String sql = "INSERT INTO dishes (seller_id, dish_name, description, price, stock_quantity) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, dish.getSellerId());
            stmt.setString(2, dish.getDishName());
            stmt.setString(3, dish.getDescription());
            stmt.setDouble(4, dish.getPrice());
            stmt.setInt(5, dish.getStockQuantity());
            stmt.executeUpdate();

            // read back generated dish_id
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    dish.setDishId(keys.getInt(1));
                }
            }
        }
    }

    /** Update an existing dish’s details (except stock). */
    public void updateDish(Dish dish) throws SQLException {
        String sql = "UPDATE dishes SET dish_name = ?, description = ?, price = ? WHERE dish_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dish.getDishName());
            stmt.setString(2, dish.getDescription());
            stmt.setDouble(3, dish.getPrice());
            stmt.setInt(4, dish.getDishId());
            stmt.executeUpdate();
        }
    }

    /** Delete a dish by its ID. */
    public void deleteDish(int dishId) throws SQLException {
        String sql = "DELETE FROM dishes WHERE dish_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dishId);
            stmt.executeUpdate();
        }
    }

    /** Fetch a single dish by its ID. */
    public Dish getDishById(int dishId) throws SQLException {
        String sql = "SELECT seller_id, dish_name, description, price, stock_quantity FROM dishes WHERE dish_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dishId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Dish d = new Dish();
                    d.setDishId(dishId);
                    d.setSellerId(rs.getInt("seller_id"));
                    d.setDishName(rs.getString("dish_name"));
                    d.setDescription(rs.getString("description"));
                    d.setPrice(rs.getDouble("price"));
                    d.setStockQuantity(rs.getInt("stock_quantity"));
                    return d;
                }
            }
        }
        return null;
    }

    /** List all dishes offered by a particular seller. */
    public List<Dish> getDishesBySellerId(int sellerId) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT dish_id, dish_name, description, price, stock_quantity FROM dishes WHERE seller_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Dish d = new Dish();
                    d.setDishId(rs.getInt("dish_id"));
                    d.setSellerId(sellerId);
                    d.setDishName(rs.getString("dish_name"));
                    d.setDescription(rs.getString("description"));
                    d.setPrice(rs.getDouble("price"));
                    d.setStockQuantity(rs.getInt("stock_quantity"));
                    dishes.add(d);
                }
            }
        }
        return dishes;
    }

    /** Reduce stock after an order. */
    public boolean decrementStock(int dishId, int amount) throws SQLException {
        String sql = "UPDATE dishes SET stock_quantity = stock_quantity - ? WHERE dish_id = ? AND stock_quantity >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, dishId);
            stmt.setInt(3, amount);
            int updated = stmt.executeUpdate();
            return updated == 1;
        }
    }

    /** List all dishes purchased by a given customer. */
    public List<Dish> getDishesByCustomerId(int customerId) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        String sql =
            "SELECT d.dish_id, d.seller_id, d.dish_name, d.description, d.price, d.stock_quantity " +
            "FROM sold_dishes sd " +
            "JOIN dishes d ON sd.dish_id = d.dish_id " +
            "WHERE sd.customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Dish d = new Dish();
                    d.setDishId(rs.getInt("dish_id"));
                    d.setSellerId(rs.getInt("seller_id"));
                    d.setDishName(rs.getString("dish_name"));
                    d.setDescription(rs.getString("description"));
                    d.setPrice(rs.getDouble("price"));
                    d.setStockQuantity(rs.getInt("stock_quantity"));
                    d.setCustomerId(customerId);
                    dishes.add(d);
                }
            }
        }
        return dishes;
    }
}
