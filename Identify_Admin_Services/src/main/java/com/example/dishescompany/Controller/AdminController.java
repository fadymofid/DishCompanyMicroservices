// src/main/java/com/example/identityadmin/controller/AdminController.java
package com.example.dishescompany.Controller;

import com.example.dishescompany.DTO.*;
import com.example.dishescompany.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Bulk-create seller credentials and notify Seller service.
     * Returns the generated username/password pairs.
     */
    @PostMapping("/sellers")
    public ResponseEntity <SellerAccountDTO> createSellers(
            @RequestBody CreateSellerRequest req) {
        SellerAccountDTO dto = adminService.createSellerAccount(req);
        return ResponseEntity.ok(dto);
    }

    /**
     * List all registered customers.
     */
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> listCustomers() {
        return ResponseEntity.ok(adminService.listCustomers());
    }

    /**
     * List all seller domain records.
     */
    @GetMapping("/sellers")
    public ResponseEntity<List<SellerDTO>> listSellers() {
        return ResponseEntity.ok(adminService.listSellers());
    }

    /**
     * Create a new shipping company (delegates to Shipping service).
     */
    @PostMapping("/shipping/companies")
    public ResponseEntity<Void> createShippingCompany(
            @RequestBody ShippingCompanyRequest req) {
        adminService.createShippingCompany(req);
        return ResponseEntity.status(201).build();
    }

    /**
     * List all shipping companies.
     */
    @GetMapping("/shipping/companies")
    public ResponseEntity<List<ShippingCompanyResponse>> listShippingCompanies() {
        return ResponseEntity.ok(adminService.listShippingCompanies());
    }
}
