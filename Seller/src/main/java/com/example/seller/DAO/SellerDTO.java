package com.example.seller.DAO;

public class SellerDTO {
        private String companyName;
        private String username;
        private String password;

        // Getters and Setters (required for Jackson)
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }