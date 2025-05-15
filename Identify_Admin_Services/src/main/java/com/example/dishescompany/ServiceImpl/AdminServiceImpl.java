package com.example.dishescompany.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.dishescompany.DTO.CreateSellerRequest;
import com.example.dishescompany.DTO.CustomerDTO;
import com.example.dishescompany.DTO.SellerAccountDTO;
import com.example.dishescompany.DTO.SellerDTO;
import com.example.dishescompany.Models.Role;
import com.example.dishescompany.Models.Seller;
import com.example.dishescompany.Repo.CustomerRepository;
import com.example.dishescompany.Repo.UserRepository;
import com.example.dishescompany.Service.AdminService;
import com.example.dishescompany.config.RabbitConfig;

@Service
@SuppressWarnings("deprecation")
public class AdminServiceImpl implements AdminService {

    private static final int PASSWORD_LENGTH = 8;

    private final UserRepository    userRepo;
    private final CustomerRepository customerRepo;
    private final RabbitTemplate    rabbitTemplate;

    public AdminServiceImpl(UserRepository userRepo,
                            CustomerRepository customerRepo,
                            RabbitTemplate rabbitTemplate) {
        this.userRepo        = userRepo;
        this.customerRepo    = customerRepo;
        this.rabbitTemplate  = rabbitTemplate;
    }

    @Override
    public SellerAccountDTO createSellerAccount(CreateSellerRequest req) {
        // 1) validate input
        if (req.getCompanyName() == null || req.getCompanyName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Company name must be provided");
        }
        if (req.getUsername() == null || req.getUsername().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Username must be provided");
        }
        // 2) ensure username not used
        if (userRepo.findByUsername(req.getUsername()) != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username already exists");
        }

        // 3) generate password & persist locally
        String rawPassword = RandomStringUtils.randomAlphanumeric(PASSWORD_LENGTH);
        SellerAccountDTO dto = new SellerAccountDTO(
                req.getCompanyName(),
                req.getUsername(),
                rawPassword
        );
        // we store minimal credential info here
        Seller s = new Seller(req.getUsername(), rawPassword, req.getCompanyName());
        userRepo.save(s);

        // 4) publish a "seller.created" event
        rabbitTemplate.convertAndSend(
                RabbitConfig.ADMIN_EXCHANGE,
                RabbitConfig.ROUTING_CREATE_SELLER,
                dto
        );

        return dto;
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepo.findAll().stream()
                .map(c -> new CustomerDTO(
                        c.getId(),
                        c.getUsername(),
                        c.getAddress()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SellerDTO> listSellers() {
        return userRepo.findByRole(Role.SELLER).stream()
                .map(u -> new SellerDTO(u.getId(), ((Seller)u).getCompanyName(), u.getUsername()))
                .collect(Collectors.toList());
    }
}
