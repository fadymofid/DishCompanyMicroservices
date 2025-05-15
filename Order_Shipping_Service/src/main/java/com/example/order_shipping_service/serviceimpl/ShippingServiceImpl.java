package com.example.order_shipping_service.serviceimpl;

import com.example.order_shipping_service.config.RabbitConfig;
import com.example.order_shipping_service.DTO.ShippingCompanyRequest;
import com.example.order_shipping_service.DTO.ShippingCompanyResponse;
import com.example.order_shipping_service.Models.ShippingCompany;
import com.example.order_shipping_service.Repo.ShippingCompanyRepository;
import com.example.order_shipping_service.service.ShippingService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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
                    // Publish failure on the order-exchange:
                    rabbitTemplate.convertAndSend(
                            RabbitConfig.ORDER_EXCHANGE,
                            "shipping.failed",
                            "Company not found: " + companyId
                    );
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Shipping company not found");
                });

        boolean valid = company.getCoverageRegions().contains(region);
        if (!valid) {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.ORDER_EXCHANGE,
                    "shipping.failed",
                    "Region not covered by company " + company.getName()
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Shipping region not covered");
        }
        return true;
    }

    @Override
    @Transactional
    public ShippingCompanyResponse createShippingCompany(ShippingCompanyRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name must not be blank");
        }

        ShippingCompany company = new ShippingCompany();
        company.setName(request.getName());
        company.setCoverageRegions(request.getCoverageRegions());
        ShippingCompany saved = shippingCompanyRepository.save(company);

        // Notify via RabbitMQ that a new company was created
        rabbitTemplate.convertAndSend(
                RabbitConfig.ORDER_EXCHANGE,
                "shipping.created",
                "Created shipping company '" + saved.getName() + "' (id=" + saved.getId() + ")"
        );

        return new ShippingCompanyResponse(
                saved.getId(),
                saved.getName(),
                saved.getCoverageRegions()
        );
    }

    @Override
    public ShippingCompany getShippingCompany(Long id) {
        return shippingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipping company not found"));
    }

    @Override
    public BigDecimal calculateShippingFee(Long shippingCompanyId, String customerRegion) {
        ShippingCompany company = shippingCompanyRepository.findById(shippingCompanyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipping company not found"));

        if (!company.getCoverageRegions().contains(customerRegion)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Region not covered by this shipping company");
        }

        // Example: Calculate fee based on region or other factors
        return new BigDecimal("10.00"); // Replace with dynamic logic if needed
    }

    @Override
    public List<ShippingCompanyResponse> getAllShippingCompanies() {
        return shippingCompanyRepository.findAll()
                .stream()
                .map(company -> new ShippingCompanyResponse(company.getId(), company.getName(), company.getCoverageRegions()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ShippingCompanyResponse> getCompaniesByRegion(String region) {
        List<ShippingCompany> companies = shippingCompanyRepository.findByRegionsCoveredContaining(region);

        return companies.stream()
                .map(company -> new ShippingCompanyResponse(company.getId(), company.getName(), company.getCoverageRegions()))
                .collect(Collectors.toList());
    }
}
