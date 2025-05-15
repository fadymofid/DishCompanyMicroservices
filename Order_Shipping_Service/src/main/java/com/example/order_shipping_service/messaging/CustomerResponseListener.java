package com.example.order_shipping_service.messaging;

import com.example.order_shipping_service.DTO.CustomerDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomerResponseListener {

    private final ConcurrentHashMap<Long, CustomerDTO> customerResponseCache = new ConcurrentHashMap<>();

    @RabbitListener(queues = "customer.response.queue")
    public void handleCustomerResponse(CustomerDTO customer) {
        // Cache the customer response
        customerResponseCache.put(customer.getId(), customer);
    }

    public CustomerDTO getCustomerResponse(Long customerId) {
        return customerResponseCache.remove(customerId); // Remove after retrieving to avoid stale data
    }
}