package com.example.order_shipping.Repo;

import com.example.order_shipping.Models.ShippingCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingCompanyRepository extends JpaRepository<ShippingCompany, Long> {
    // nothing extra needed—JPA provides findById, save, etc.
}
