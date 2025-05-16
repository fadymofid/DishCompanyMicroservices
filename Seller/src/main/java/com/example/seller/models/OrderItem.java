package com.example.seller.models;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @JoinColumn(name = "order_id")
    private int order_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id")
    private Dish dish;

    private Integer quantity;

    private BigDecimal priceAtOrder;

    // Required no-arg constructor
    public OrderItem() {}

    // Convenience constructor
    public OrderItem(Dish dish, Integer quantity) {
        this.dish = dish;
        this.quantity = quantity;
        this.priceAtOrder = dish.getPrice(); // Capture price at time of order
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

   

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(BigDecimal priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    // Helper method to calculate line total
//    public BigDecimal getLineTotal() {
//        return priceAtOrder.multiply(BigDecimal.valueOf(quantity));
//    }
}