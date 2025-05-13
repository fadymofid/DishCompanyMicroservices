package com.example.dishescompany.Service;

import com.example.dishescompany.DTO.CustomerDTO;
import com.example.dishescompany.DTO.SellerAccountDTO;
import com.example.dishescompany.DTO.SellerDTO;

import java.util.List;

public interface AdminService {
    /**
     * Create seller representative accounts for a list of unique company names.
     * Returns generated username/password pairs.
     */
    List<SellerAccountDTO> createSellerAccounts(List<String> companyNames);

    /**
     * List all registered customers.
     */
    List<CustomerDTO> listCustomers();

    /**
     * List all seller representative accounts.
     */
    List<SellerDTO> listSellers();
}