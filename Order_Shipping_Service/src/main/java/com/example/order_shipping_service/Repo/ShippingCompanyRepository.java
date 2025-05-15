package com.example.order_shipping_service.Repo;

import com.example.order_shipping_service.Models.ShippingCompany;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingCompanyRepository extends JpaRepository<ShippingCompany, Long> {

    List<ShippingCompany> findByRegionsCoveredContaining(String region);
    // nothing extra needed—JPA provides findById, save, etc.
}
