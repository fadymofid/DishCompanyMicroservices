package com.example.dishescompany.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sellers")
public class Seller extends User {
    @Column(nullable = false)
    private String companyName;

    public Seller() {
        super();
        setRole(Role.SELLER);
    }

    public Seller(String username, String password, String companyName) {
        super(username, password, Role.SELLER);
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
