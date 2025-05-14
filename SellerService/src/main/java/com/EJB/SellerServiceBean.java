package com.EJB;


import com.models.Dish;
import com.models.Seller;
import com.DAO.DishDAO;
import com.DAO.SellerDAO;
import jakarta.ejb.EJB;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
@Remote(SellerService.class)
public class SellerServiceBean implements SellerService {

    @EJB
    private DishDAO dishDAO;
    @EJB
    private SellerDAO sellerDAO;

    @Override
    public List<Dish> getMyDishes(Long sellerId) {
        return dishDAO.findBySeller(sellerId);
    }

    @Override
    public void addDish(Long sellerId, Dish dish) {
        Seller s = sellerDAO.find(sellerId);
        dish.setSeller(s);
        dishDAO.save(dish);
    }

    @Override
    public void updateDish(Long sellerId, Dish dish) {
        Dish existing = dishDAO.find(dish.getId());
        if (!existing.getSeller().getId().equals(sellerId)) {
            throw new SecurityException("Not your dish");
        }
        existing.setName(dish.getName());
        existing.setPrice(dish.getPrice());
        existing.setStock(dish.getStock());
        dishDAO.update(existing);
    }
}
