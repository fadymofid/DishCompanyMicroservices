package com.example.order_payment.dto;

public class StockCheckResponse {
    private String correlationId;
    private boolean available;

    // Getters and setters
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
}