package com.example.order_payment.dto;

public class PaymentRequest {
    private String correlationId;
    private Long customerId;      // ← renamed
    private Long orderId;         // ← still include for order reference
    private double amount;

    public PaymentRequest() {}

    public PaymentRequest(String correlationId, Long customerId, Long orderId, double amount) {
        this.correlationId = correlationId;
        this.customerId    = customerId;
        this.orderId       = orderId;
        this.amount        = amount;
    }

    // getters & setters
    public String getCorrelationId()     { return correlationId; }
    public void setCorrelationId(String c) { this.correlationId = c; }

    public Long getCustomerId()          { return customerId; }
    public void setCustomerId(Long c)      { this.customerId = c; }

    public Long getOrderId()             { return orderId; }
    public void setOrderId(Long id)       { this.orderId = id; }

    public double getAmount()            { return amount; }
    public void setAmount(double a)       { this.amount = a; }
}
