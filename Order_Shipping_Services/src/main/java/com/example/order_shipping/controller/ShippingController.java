package com.example.order_shipping.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.order_shipping.DTO.ShippingCompanyRequest;
import com.example.order_shipping.DTO.ShippingCompanyResponse;
import com.example.order_shipping.service.ShippingService;
import java.util.List;

@RestController
@RequestMapping("/shipping")
public class ShippingController {
    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Void> createShippingCompany(@RequestBody ShippingCompanyRequest request) {
        shippingService.createShippingCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/companies")
    public List<ShippingCompanyResponse> listShippingCompanies() {
        return shippingService.getAllShippingCompanies();
    }
}