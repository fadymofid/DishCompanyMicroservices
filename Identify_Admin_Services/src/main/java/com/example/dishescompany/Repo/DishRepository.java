package com.example.dishescompany.Repo;

import com.example.dishescompany.Models.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for menu items.
 */
@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    // find all by seller
    List<Dish> findBySellerId(Long sellerId);
}
