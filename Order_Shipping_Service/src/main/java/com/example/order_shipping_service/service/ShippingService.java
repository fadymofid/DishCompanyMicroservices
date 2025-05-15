package com.example.order_shipping_service.service;

import com.example.order_shipping_service.DTO.ShippingCompanyRequest;
import com.example.order_shipping_service.DTO.ShippingCompanyResponse;
import com.example.order_shipping_service.Models.ShippingCompany;

import java.math.BigDecimal;
import java.util.List;

public interface ShippingService {
    boolean validateShippingRegion(Long companyId, String region) ;
    ShippingCompanyResponse createShippingCompany(ShippingCompanyRequest request);
    ShippingCompany getShippingCompany(Long id);
    public BigDecimal calculateShippingFee(Long shippingCompanyId, String customerRegion);
    List<ShippingCompanyResponse> getAllShippingCompanies();
    List<ShippingCompanyResponse> getCompaniesByRegion(String region);
}