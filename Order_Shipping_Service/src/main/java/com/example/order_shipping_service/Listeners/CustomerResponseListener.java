package com.example.order_shipping_service.Listeners;

import com.example.order_shipping_service.DTO.CustomerDTO;
import com.example.order_shipping_service.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomerResponseListener {

    private final ConcurrentHashMap<Long, CustomerDTO> customerResponseCache = new ConcurrentHashMap<>();
    @RabbitListener(queues = RabbitConfig.CUSTOMER_RESPONSE_QUEUE)
    public void handle(CustomerDTO dto) {
        customerResponseCache.put(dto.getId(), dto);
    }

    /**
     * Blocks up to timeoutMillis for a matching CustomerDTO.
     * @return the DTO or null if not received in time.
     */
    public CustomerDTO waitForCustomerResponse(Long id, long timeoutMillis) {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            CustomerDTO dto = customerResponseCache.remove(id);
            if (dto != null) return dto;
            try { Thread.sleep(50); } catch(InterruptedException e){ Thread.currentThread().interrupt(); break; }
        }
        return null;
    }
    @RabbitListener(queues = "customer.response.queue")
    public void handleCustomerResponse(CustomerDTO customer) {
        // Cache the customer response
        customerResponseCache.put(customer.getId(), customer);
    }

    public CustomerDTO getCustomerResponse(Long customerId) {
        return customerResponseCache.remove(customerId); // Remove after retrieving to avoid stale data
    }
}