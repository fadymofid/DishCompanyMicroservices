package com.homemade.ordersAndShipmentService.dto;

public class PaymentResponse {
    private String correlationId;
    private boolean success;
    private String failureReason;

    public PaymentResponse() {}

    public PaymentResponse(String correlationId, boolean success, String failureReason) {
        this.correlationId = correlationId;
        this.success = success;
        this.failureReason = failureReason;
    }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}