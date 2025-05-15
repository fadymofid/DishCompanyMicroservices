// src/main/java/com/example/identityadmin/controller/AdminController.java
package com.example.dishescompany.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dishescompany.DTO.CreateSellerRequest;
import com.example.dishescompany.DTO.CustomerDTO;
import com.example.dishescompany.DTO.SellerAccountDTO;
import com.example.dishescompany.DTO.SellerDTO;
import com.example.dishescompany.Service.AdminService;

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
}
