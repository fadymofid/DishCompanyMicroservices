package com.example.dishescompany.Controller;

import com.example.dishescompany.DTO.CustomerDTO;
import com.example.dishescompany.DTO.SellerAccountDTO;
import com.example.dishescompany.DTO.SellerDTO;
import com.example.dishescompany.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/sellers")
    public ResponseEntity<List<SellerAccountDTO>> createSellers(@RequestBody List<String> companyNames) {
        List<SellerAccountDTO> dtos = adminService.createSellerAccounts(companyNames);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> getCustomers() {
        return ResponseEntity.ok(adminService.listCustomers());
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<SellerDTO>> getSellers() {
        return ResponseEntity.ok(adminService.listSellers());
    }
}