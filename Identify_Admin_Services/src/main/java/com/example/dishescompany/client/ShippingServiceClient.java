package com.example.dishescompany.client;



import com.example.dishescompany.DTO.ShippingCompanyRequest;
import com.example.dishescompany.DTO.ShippingCompanyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class ShippingServiceClient {
    private final RestTemplate restTemplate;
    private final String shippingBaseUrl;

    public ShippingServiceClient(RestTemplate restTemplate,
                                 @Value("${shipping.service.url}") String shippingBaseUrl) {
        this.restTemplate    = restTemplate;
        this.shippingBaseUrl = shippingBaseUrl;
    }

    public void createCompany(ShippingCompanyRequest req) {
        restTemplate.postForEntity(
                shippingBaseUrl + "/shipping/companies",
                req,
                Void.class
        );
    }

    public List<ShippingCompanyResponse> getAllCompanies() {
        ResponseEntity<ShippingCompanyResponse[]> resp = restTemplate.getForEntity(
                shippingBaseUrl + "/shipping/companies",
                ShippingCompanyResponse[].class
        );
        return Arrays.asList(resp.getBody());
    }
}

