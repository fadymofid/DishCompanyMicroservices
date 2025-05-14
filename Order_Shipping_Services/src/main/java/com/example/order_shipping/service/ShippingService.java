package com.example.order_shipping.service;

import com.example.order_shipping.DTO.ShippingCompanyRequest;

import com.example.order_shipping.Models.ShippingCompany;

public interface ShippingService {
    boolean validateShippingRegion(Long companyId, String region) ;
    void createShippingCompany(ShippingCompanyRequest request);
    ShippingCompany getShippingCompany(Long id);

}