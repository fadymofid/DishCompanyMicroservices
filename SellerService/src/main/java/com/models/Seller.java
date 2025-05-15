// src/main/java/com/example/dishescompany/Models/Seller.java
package com.models;

import com.models.User;
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

    // getter/setter for companyName
}
