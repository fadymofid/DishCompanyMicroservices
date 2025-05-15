package com.example.dishescompany.Lisstener;
import com.example.dishescompany.config.RabbitConfig;
import com.example.dishescompany.DTO.CustomerDTO;
import com.example.dishescompany.Models.Customer;
import com.example.dishescompany.Repo.CustomerRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class CustomerLookupListener {

    private final CustomerRepository customerRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CustomerLookupListener(CustomerRepository customerRepository,
                                  RabbitTemplate rabbitTemplate) {
        this.customerRepository = customerRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.CUSTOMER_REQUEST_QUEUE)
    public void handleCustomerRequest(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        CustomerDTO dto = new CustomerDTO(
                customer.getId(),
                customer.getUsername(),
                customer.getAddress()
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.CUSTOMER_EXCHANGE,
                "customer.response",
                dto
        );
    }
}