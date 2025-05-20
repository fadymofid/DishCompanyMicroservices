package com.homemade.ordersAndShipmentService.dto;

public class PaymentRequest {
    private String correlationId;
    private Long orderId;
    private double amount;

    public PaymentRequest() {}

    public PaymentRequest(String correlationId, Long orderId, double amount) {
        this.correlationId = correlationId;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}