package com.example.order_shipping.DTO;

public class CustomerDTO {
    private Long id;
    private String username;
    private String address;

    public CustomerDTO() {
    }

    public CustomerDTO(Long id, String username, String address) {
        this.id = id;
        this.username = username;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
// getters & setters
}
