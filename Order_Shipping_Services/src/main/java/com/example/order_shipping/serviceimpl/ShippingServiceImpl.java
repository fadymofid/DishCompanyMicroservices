package com.example.order_shipping.serviceimpl;

import com.example.order_shipping.DTO.ShippingCompanyRequest;
import com.example.order_shipping.DTO.ShippingCompanyResponse;
import com.example.order_shipping.Models.ShippingCompany;
import com.example.order_shipping.Repo.ShippingCompanyRepository;
import com.example.order_shipping.service.ShippingService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShippingServiceImpl implements ShippingService {

    private final ShippingCompanyRepository shippingCompanyRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ShippingServiceImpl(ShippingCompanyRepository shippingCompanyRepository,
                               RabbitTemplate rabbitTemplate) {
        this.shippingCompanyRepository = shippingCompanyRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public boolean validateShippingRegion(Long companyId, String region) {
        ShippingCompany company = shippingCompanyRepository.findById(companyId)
                .orElseThrow(() -> {
                    // Notify via RabbitMQ
                    rabbitTemplate.convertAndSend(
                            "order-exchange",
                            "shipping.failed",
                            String.format("Company %d not found", companyId)
                    );
                    // Then throw 404
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Shipping company not found");
                });

        boolean valid = company.getCoverageRegions().contains(region);
        if (!valid) {
            // send a message about failure
            rabbitTemplate.convertAndSend(
                    "order-exchange",
                    "shipping.failed",
                    String.format("Region '%s' not covered by company %s",
                            region, company.getName())
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Shipping region not covered");
        }
        return true;
    }

    @Override
    @Transactional
    public void createShippingCompany(ShippingCompanyRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name must not be blank");
        }
        ShippingCompany company = new ShippingCompany();
        company.setName(request.getName());
        company.setCoverageRegions(request.getCoverageRegions());
        shippingCompanyRepository.save(company);

        // Optionally notify via RabbitMQ that a new company was created
        rabbitTemplate.convertAndSend(
                "order-exchange",
                "shipping.created",
                String.format("Created shipping company '%s'", company.getName())
        );
    }
    @Override
    public ShippingCompany getShippingCompany(Long id) {
        return shippingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Shipping company not found"));
    }

    @Override
    public List<ShippingCompanyResponse> getAllShippingCompanies() {
        return shippingCompanyRepository.findAll()
                .stream()
                .map(company -> new ShippingCompanyResponse(
                        company.getId(),
                        company.getName(),
                        company.getCoverageRegions()
                ))
                .collect(Collectors.toList());
    }


}
