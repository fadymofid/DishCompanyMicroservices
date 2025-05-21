package com.homemade.ordersAndShipmentService.dto;

import java.util.List;

import com.homemade.ordersAndShipmentService.entity.OrderItem;

public class OrderRequest {
    private Long customerId;
    private List<OrderItem> items;
private String address; // Add this field

    // Add getter/setter
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public List<OrderItem> getItems() {
        return items;
    }
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
