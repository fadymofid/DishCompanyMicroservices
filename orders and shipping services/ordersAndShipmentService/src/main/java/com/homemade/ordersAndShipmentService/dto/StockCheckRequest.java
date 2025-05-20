package com.homemade.ordersAndShipmentService.dto;

import com.homemade.ordersAndShipmentService.entity.OrderItem;
import java.util.List;

public class StockCheckRequest {
    private String correlationId;
    private List<OrderItem> items;

    public StockCheckRequest() {}

    public StockCheckRequest(String correlationId, List<OrderItem> items) {
        this.correlationId = correlationId;
        this.items = items;
    }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}