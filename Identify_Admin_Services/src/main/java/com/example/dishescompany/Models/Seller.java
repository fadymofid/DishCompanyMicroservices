package com.example.dishescompany.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sellers")
public class Seller extends User {
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private Set<Dish> dishes = new HashSet<>();

    // reference to the company they represent
    @Column(nullable = false)
    private String companyName;

    // getters/setters
}
