package com.example.order_shipping.Models;

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
@Table(name = "shipping_companies")
public class ShippingCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ElementCollection
    @CollectionTable(name = "shipping_coverage", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "region")
    private Set<String> coverageRegions = new HashSet<>();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getCoverageRegions() {
        return coverageRegions;
    }

    public void setCoverageRegions(Set<String> coverageRegions) {
        this.coverageRegions = coverageRegions;
    }
}