package com.example.dishescompany.client;


import com.example.dishescompany.DTO.SellerAccountDTO;
import com.example.dishescompany.DTO.SellerDTO;
import com.example.dishescompany.DTO.CreateSellerRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class SellerServiceClient {
    private final RestTemplate restTemplate;
    private final String sellerBaseUrl;

    public SellerServiceClient(RestTemplate restTemplate,
                               @Value("${seller.service.url}") String sellerBaseUrl) {
        this.restTemplate = restTemplate;
        this.sellerBaseUrl = sellerBaseUrl;
    }



    public void createSeller(SellerAccountDTO sellerAccountDTO)
    {
        String companyName = sellerAccountDTO.getCompanyName();
        String username = sellerAccountDTO.getUsername();
        String password = sellerAccountDTO.getPassword();
        CreateSellerRequest req = new CreateSellerRequest(companyName, username, password);
        restTemplate.postForEntity(sellerBaseUrl + "/seller", req, Void.class);
    }


    public List<SellerDTO> getAllSellers() {
        ResponseEntity<SellerDTO[]> resp = restTemplate.getForEntity(
                sellerBaseUrl + "/seller",
                SellerDTO[].class
        );
        return Arrays.asList(resp.getBody());
    }
}
