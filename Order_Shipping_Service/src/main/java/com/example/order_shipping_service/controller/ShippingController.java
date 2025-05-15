package com.example.order_shipping_service.controller;

import org.springframework.web.bind.annotation.*;

import com.example.order_shipping_service.DTO.ShippingCompanyRequest;
import com.example.order_shipping_service.DTO.ShippingCompanyResponse;
import com.example.order_shipping_service.service.ShippingService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;



@RestController
@RequestMapping("/shipping/companies")
public class ShippingController {
    
    @Autowired
    private ShippingService shippingService;

    /**
     * Get all shipping companies that cover a specific region.
     * @param region The region to filter shipping companies by.
     * @return A list of shipping companies that cover the specified region.
     */
    @GetMapping("/{region}")
    public List<ShippingCompanyResponse> getCompaniesByRegion(@PathVariable String region) {
        return shippingService.getCompaniesByRegion(region);
    }

    /**
     * Create a new shipping company.
     * @param request The request containing the shipping company details.
     * @return The response with the created shipping company details.
     */
    @PostMapping
    public ShippingCompanyResponse createShippingCompany(@RequestBody ShippingCompanyRequest request) {
        return shippingService.createShippingCompany(request);
    }
}