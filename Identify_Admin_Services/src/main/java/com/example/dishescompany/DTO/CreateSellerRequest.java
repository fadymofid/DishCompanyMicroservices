package com.example.dishescompany.DTO;



public class CreateSellerRequest {
    private String companyName;
    private String username;
    private String password;

    public CreateSellerRequest(String companyName, String username, String password) {
        this.companyName = companyName;
        this.username = username;
        this.password = password;
    }

    public CreateSellerRequest(String companyName, String username) {
        this.companyName = companyName;
        this.username = username;

    }
  public CreateSellerRequest() {}

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}

