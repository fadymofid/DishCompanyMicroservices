package com.homemade.ordersAndShipmentService.dto;

public class StockCheckResponse {
    private String correlationId;
    private boolean available;

    // No-arg constructor for Jackson
    public StockCheckResponse() {}

    public String getCorrelationId() {
        return correlationId;
    }
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "StockCheckResponse{" +
                "correlationId='" + correlationId + '\'' +
                ", available=" + available +
                '}';
    }
}