package com.example.order_shipping.service;

import com.example.order_shipping.DTO.OrderItemRequest;
import com.example.order_shipping.DTO.OrderRequest;
import com.example.order_shipping.DTO.OrderResponse;
import com.example.order_shipping.DTO.CustomerDTO;
import com.example.order_shipping.DTO.DishDTO;
import com.example.order_shipping.Models.Order;
import com.example.order_shipping.Models.OrderItem;
import com.example.order_shipping.Models.OrderStatus;
import com.example.order_shipping.Repo.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private static final BigDecimal MINIMUM_CHARGE = new BigDecimal("20.00");
    private static final long RESPONSE_TIMEOUT_SECONDS = 5; // Timeout for RabbitMQ responses

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ShippingService shippingService;

    // Blocking queues for simulating RabbitMQ responses
    private final BlockingQueue<CustomerDTO> customerResponseQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<DishDTO> dishResponseQueue = new LinkedBlockingQueue<>();

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            RabbitTemplate rabbitTemplate,
                            ShippingService shippingService) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.shippingService = shippingService;
    }

    @Override
    @Transactional
    public OrderResponse processOrder(OrderRequest req) {
        // Publish a message to request customer details
        rabbitTemplate.convertAndSend("customer-exchange", "customer.request", req.getCustomerId());

        // Wait for the response (synchronous for simplicity)
        CustomerDTO customer = waitForCustomerResponse(req.getCustomerId());
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }

        // Validate shipping region
        shippingService.validateShippingRegion(req.getShippingCompanyId(), customer.getAddress());

        // Process the order
        BigDecimal itemsTotal = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : req.getItems()) {
            // Publish a message to request dish details
            rabbitTemplate.convertAndSend("dish-exchange", "dish.request", itemRequest.getDishId());

            // Wait for the response (synchronous for simplicity)
            DishDTO dish = waitForDishResponse(itemRequest.getDishId());
            if (dish == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found");
            }
            if (dish.getStock() < itemRequest.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock for dish: " + dish.getName());
            }

            // Calculate total price for the item
            itemsTotal = itemsTotal.add(dish.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        // Calculate shipping fee
        BigDecimal shippingFee = shippingService.calculateShippingFee(req.getShippingCompanyId(), customer.getAddress());
        BigDecimal total = itemsTotal.add(shippingFee);

        if (total.compareTo(MINIMUM_CHARGE) < 0) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Below minimum charge");
        }

        // Persist order
        Order order = new Order();
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        // Notify customer
        rabbitTemplate.convertAndSend("order-exchange", "order.confirmed", order.getId());
        return new OrderResponse(order.getId(), total, order.getStatus());
    }

    private CustomerDTO waitForCustomerResponse(Long customerId) {
        try {
            // Wait for the response with a timeout
            CustomerDTO customer = customerResponseQueue.poll(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (customer == null || !customer.getId().equals(customerId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer response not received or mismatched");
            }
            return customer;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Interrupted while waiting for customer response");
        }
    }

    private DishDTO waitForDishResponse(Long dishId) {
        try {
            // Wait for the response with a timeout
            DishDTO dish = dishResponseQueue.poll(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (dish == null || !dish.getId().equals(dishId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish response not received or mismatched");
            }
            return dish;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Interrupted while waiting for dish response");
        }
    }

    // Simulated RabbitMQ listeners for testing
    @RabbitListener(queues = "customer.response.queue")
    public void handleCustomerResponse(CustomerDTO customer) {
        customerResponseQueue.offer(customer); // Add response to the queue
    }

    @RabbitListener(queues = "dish.response.queue")
    public void handleDishResponse(DishDTO dish) {
        dishResponseQueue.offer(dish); // Add response to the queue
    }

    @Override
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);

        return orders.stream()
                .map(order -> new OrderResponse(order.getId(), order.getTotalAmount(), order.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public OrderResponse getOrderDetails(String orderId) {
        Order order = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        return new OrderResponse(order.getId(), order.getTotalAmount(), order.getStatus());
    }
}
