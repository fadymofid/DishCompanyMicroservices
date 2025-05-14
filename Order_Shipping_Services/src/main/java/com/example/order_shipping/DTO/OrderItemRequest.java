package com.example.order_shipping.DTO;



import java.math.BigDecimal;

public class OrderItemRequest {
    private Long dishId;
    private Integer quantity;
    private BigDecimal price;

    public OrderItemRequest() {}

    public OrderItemRequest(Long dishId, Integer quantity, BigDecimal price) {
        this.dishId   = dishId;
        this.quantity = quantity;
        this.price    = price;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
