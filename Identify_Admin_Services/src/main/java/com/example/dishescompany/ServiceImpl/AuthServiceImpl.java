package com.example.dishescompany.ServiceImpl;

import com.example.dishescompany.DTO.LoginRequest;
import com.example.dishescompany.DTO.RegisterRequest;
import com.example.dishescompany.Models.Customer;
import com.example.dishescompany.Models.Seller;
import com.example.dishescompany.Repo.CustomerRepository;
import com.example.dishescompany.Repo.SellerRepository;
import com.example.dishescompany.Repo.UserRepository;
import com.example.dishescompany.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private SellerRepository sellerRepository;

    @Override
    public void register(RegisterRequest req) {
        if (userRepository.findByUsername(req.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        switch (req.getRole().toUpperCase()) {
            case "CUSTOMER":
                Customer customer = new Customer();
                customer.setUsername(req.getUsername());
                customer.setPassword(req.getPassword()); // No encoding
                customer.setAddress("");
                customerRepository.save(customer);
                break;
            case "SELLER":
                Seller seller = new Seller();
                seller.setUsername(req.getUsername());
                seller.setPassword(req.getPassword()); // No encoding
                seller.setCompanyName("");
                sellerRepository.save(seller);
                break;
            default:
                throw new IllegalArgumentException("Invalid role");
        }
    }

    @Override
    public void login(LoginRequest req) {
        // Check against both repositories (or only one, depending on your app)
        Customer customer = customerRepository.findByUsername(req.getUsername());
        if (customer != null && customer.getPassword().equals(req.getPassword())) {
            return; // Login successful
        }

        Seller seller = sellerRepository.findByUsername(req.getUsername());
        if (seller != null && seller.getPassword().equals(req.getPassword())) {
            return; // Login successful
        }

        throw new IllegalArgumentException("Invalid username or password");
    }
}
