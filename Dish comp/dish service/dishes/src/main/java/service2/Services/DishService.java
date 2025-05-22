package service2.Services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import jakarta.ejb.Stateless;
import service2.DAL.DatabaseConnection;
import service2.DAL.DishDAL;
import service2.Model.Dish;

@Stateless
public class DishService {

    private DishDAL dishDAL;

    public DishService() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        this.dishDAL = new DishDAL(conn);
    }

    public void addDish(Dish dish) throws SQLException {
        dishDAL.addDish(dish);
    }

    public void updateDish(Dish dish) throws SQLException {
        dishDAL.updateDish(dish);
    }

    public void deleteDish(int dishId) throws SQLException {
        dishDAL.deleteDish(dishId);
    }
    public boolean isStockAvailable(int dishId, int quantity) {
        Dish dish = null;
        try {
            dish = this.getDishById(dishId);  // call method in the same class
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if (dish == null) {
            return false;
        }
        return dish.getStockQuantity() >= quantity;
    }

    public List<Dish> getAllDishesBySeller(int sellerId) throws SQLException {
        return dishDAL.getAllDishesBySeller(sellerId);
    }

    public Dish getDishById(int id) throws SQLException {
        return dishDAL.GetDishById(id);
    }
    public String getNameById(int id) throws SQLException {
        return dishDAL.GetNameById(id);
    }

    public List<Dish> getAllDishes() throws SQLException {
        return dishDAL.getAllDishes();
    }

    public void deductStock(int dishId, int quantity) throws SQLException {
        dishDAL.deductStock(dishId, quantity);
    }
     public List<Dish> getDishesByCustomerId(int customerId) throws SQLException {
        return dishDAL.getDishesByCustomerId(customerId);
    }


    public void decrementStock(Long dishId, int quantity)throws SQLException
    {
       dishDAL.decrementStock(dishId, quantity);}



}