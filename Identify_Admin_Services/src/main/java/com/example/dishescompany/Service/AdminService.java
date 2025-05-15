package com.example.dishescompany.Service;

import java.util.List;

import com.example.dishescompany.DTO.CreateSellerRequest;
import com.example.dishescompany.DTO.CustomerDTO;
import com.example.dishescompany.DTO.SellerAccountDTO;
import com.example.dishescompany.DTO.SellerDTO;

public interface AdminService {

    SellerAccountDTO createSellerAccount (CreateSellerRequest req);

    /** List all customers (from Customer microservice) */
    List<CustomerDTO> listCustomers();

    /** List all seller domain accounts (from Seller microservice) */
    List<SellerDTO> listSellers();
}
