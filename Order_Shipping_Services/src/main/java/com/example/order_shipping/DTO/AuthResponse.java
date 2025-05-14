package com.example.order_shipping.DTO;

public class AuthResponse {
    private String message;

    public AuthResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuthResponse(String message) {
        this.message = message;
    }
}
