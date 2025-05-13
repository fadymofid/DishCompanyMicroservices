package com.example.dishescompany.Repo;

import com.example.dishescompany.Models.ShippingCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for shipping partners.
 */
@Repository
public interface ShippingCompanyRepository extends JpaRepository<ShippingCompany, Long> {
    // find by coverage region
    List<ShippingCompany> findByCoverageRegionsContaining(String region);
}
