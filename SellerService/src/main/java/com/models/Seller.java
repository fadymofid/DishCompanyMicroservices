package com.models;

import jakarta.persistence.*;


import java.util.HashSet;
import java.util.Set;

@Entity

@Table(name = "sellers")
public class Seller extends User {
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private Set<Dish> dishes = new HashSet<>();

    // reference to the company they represent
    @Column(nullable = false)
    private String companyName;

    // JPA-required no-arg constructor
    public Seller() {
    }

    // Full constructor
    public Seller(String username, String password, String email, String companyName) {
        super(username, password);


        this.companyName = companyName;
    }

    // Convenience constructor
    public Seller(String username, String companyName) {

        this.companyName = companyName;
    }

    // Bidirectional relationship helpers
//    public void addDish(Dish dish) {
//        dishes.add(dish);
//        dish.setSeller(this);
//    }
//
//    public void removeDish(Dish dish) {
//        dishes.remove(dish);
//        dish.setSeller(null);
//    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Seller{" +
                "dishes=" + dishes +
                ", companyName='" + companyName + '\'' +
                '}';
    }
// getters/setters

    public Set<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<Dish> dishes) {
        this.dishes = dishes;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
