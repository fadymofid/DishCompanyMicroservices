package com.example.dishescompany.Service;

import com.example.dishescompany.DTO.*;

import java.util.List;


import java.util.List;

public interface AdminService {

    SellerAccountDTO createSellerAccount (CreateSellerRequest req);

    /** List all customers (from Customer microservice) */
    List<CustomerDTO> listCustomers();

    /** List all seller domain accounts (from Seller microservice) */
    List<SellerDTO> listSellers();

    /** Create a new shipping company via Shipping microservice */
    void createShippingCompany(ShippingCompanyRequest req);

    /** List all shipping companies (from Shipping microservice) */
    List<ShippingCompanyResponse> listShippingCompanies();
}
