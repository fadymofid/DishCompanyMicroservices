// src/main/java/com/example/dishescompany/ServiceImpl/AuthServiceImpl.java
package com.example.dishescompany.ServiceImpl;

import com.example.dishescompany.DTO.LoginRequest;
import com.example.dishescompany.DTO.RegisterRequest;
import com.example.dishescompany.Models.Customer;
import com.example.dishescompany.Models.Role;
import com.example.dishescompany.Models.Seller;
import com.example.dishescompany.Models.User;
import com.example.dishescompany.Repo.UserRepository;
import com.example.dishescompany.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;

    @Autowired
    public AuthServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void register(RegisterRequest req) {
        if (userRepo.findByUsername(req.getUsername()) != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username already exists");
        }

        // Always register as CUSTOMER
        Customer cust = new Customer(
                req.getUsername(),
                req.getPassword(),
                req.getAddress() != null ? req.getAddress() : ""
        );
        userRepo.save(cust);
    }

    @Override
    public String login(LoginRequest req) {
        User u = userRepo.findByUsername(req.getUsername());
        if (u == null || !u.getPassword().equals(req.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        // you may want to check req.getRole() vs u.getRole()
        return u.getRole().name();
    }
}
