package com.example.dishescompany.ServiceImpl;


import com.example.dishescompany.DTO.CustomerDTO;
import com.example.dishescompany.DTO.SellerAccountDTO;
import com.example.dishescompany.DTO.SellerDTO;
import com.example.dishescompany.Models.Customer;
import com.example.dishescompany.Models.Seller;
import com.example.dishescompany.Repo.CustomerRepository;
import com.example.dishescompany.Repo.SellerRepository;
import com.example.dishescompany.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private CustomerRepository customerRepository;



    private static final int PASSWORD_LENGTH = 12;

    @Override
    public List<SellerAccountDTO> createSellerAccounts(List<String> companyNames) {
        return companyNames.stream().map(name -> {
            // generate username and raw password
            String username = name.toLowerCase().replaceAll("\\s+", "_");
            String rawPassword = RandomStringUtils.randomAlphanumeric(PASSWORD_LENGTH);


            Seller seller = new Seller();
            seller.setCompanyName(name);
            seller.setUsername(username);
            seller.setPassword(rawPassword);
            sellerRepository.save(seller);

            return new SellerAccountDTO(name, username, rawPassword);
        }).collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll().stream()
                .map(c -> new CustomerDTO(c.getId(), c.getUsername(), c.getAddress()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SellerDTO> listSellers() {
        return sellerRepository.findAll().stream()
                .map(s -> new SellerDTO(s.getId(), s.getCompanyName(), s.getUsername()))
                .collect(Collectors.toList());
    }
}
