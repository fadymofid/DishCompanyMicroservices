package com.example.order_shipping_service.DTO;

import java.util.List;

public class OrderRequest {
    private Long orderId;               // optional, for correlating replies
    private Long customerId;
    private Long shippingCompanyId;
    private String customerRegion;
    private List<OrderItemRequest> items;

    public OrderRequest() {}

    public OrderRequest(Long orderId, Long customerId, Long shippingCompanyId,
                        String customerRegion, List<OrderItemRequest> items) {
        this.orderId           = orderId;
        this.customerId        = customerId;
        this.shippingCompanyId = shippingCompanyId;
        this.customerRegion    = customerRegion;
        this.items             = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getShippingCompanyId() {
        return shippingCompanyId;
    }

    public void setShippingCompanyId(Long shippingCompanyId) {
        this.shippingCompanyId = shippingCompanyId;
    }

    public String getCustomerRegion() {
        return customerRegion;
    }

    public void setCustomerRegion(String customerRegion) {
        this.customerRegion = customerRegion;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}