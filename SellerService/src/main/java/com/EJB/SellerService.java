package com.EJB;

import com.models.Dish;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface SellerService {

    List<Dish> getMyDishes(Long sellerId);

    void addDish(Long sellerId, Dish dish);

    void updateDish(Long sellerId, Dish dish);
}
