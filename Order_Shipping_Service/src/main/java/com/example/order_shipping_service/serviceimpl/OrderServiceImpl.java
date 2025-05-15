package com.example.order_shipping_service.serviceimpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.order_shipping_service.DTO.CustomerDTO;
import com.example.order_shipping_service.DTO.DishDTO;
import com.example.order_shipping_service.DTO.OrderItemRequest;
import com.example.order_shipping_service.DTO.OrderRequest;
import com.example.order_shipping_service.DTO.OrderResponse;
import com.example.order_shipping_service.Listeners.CustomerResponseListener;
import com.example.order_shipping_service.Listeners.DishResponseListener;
import com.example.order_shipping_service.Models.Order;
import com.example.order_shipping_service.Models.OrderStatus;
import com.example.order_shipping_service.Repo.OrderRepository;
import com.example.order_shipping_service.config.RabbitConfig;
import com.example.order_shipping_service.service.OrderService;
import com.example.order_shipping_service.service.PaymentService;

@Service
public class OrderServiceImpl implements OrderService {
    private static final BigDecimal MINIMUM_CHARGE       = new BigDecimal("20.00");
    private static final long      RESPONSE_TIMEOUT_SECS = 5;

    private final OrderRepository         orderRepository;
    private final RabbitTemplate          rabbitTemplate;
    private final CustomerResponseListener custListener;
    private final DishResponseListener     dishListener;
    private final PaymentService  paymentService;


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            RabbitTemplate rabbitTemplate,
                            CustomerResponseListener custListener,
                            DishResponseListener dishListener,
                            PaymentServiceImpl paymentService) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate  = rabbitTemplate;
        this.custListener    = custListener;
        this.dishListener    = dishListener;
        this.paymentService  = paymentService;
    }

    @Override
    @Transactional
    public OrderResponse processOrder(OrderRequest req) {
        // 1) Ask for customer details
        rabbitTemplate.convertAndSend(
                RabbitConfig.CUSTOMER_EXCHANGE,
                RabbitConfig.ROUTING_CUSTOMER_REQUEST,
                req.getCustomerId()
        );
        CustomerDTO customer = custListener.waitForCustomerResponse(
                req.getCustomerId(), TimeUnit.SECONDS.toMillis(RESPONSE_TIMEOUT_SECS)
        );
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }

        // 2) Fetch each dish & check stock
        BigDecimal itemsTotal = BigDecimal.ZERO;
        for (OrderItemRequest item : req.getItems()) {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.DISH_EXCHANGE,
                    RabbitConfig.ROUTING_DISH_REQUEST,
                    item.getDishId()
            );
            DishDTO dish = dishListener.waitForDishResponse(
                    item.getDishId(), TimeUnit.SECONDS.toMillis(RESPONSE_TIMEOUT_SECS)
            );
            if (dish == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found: " + item.getDishId());
            }
            if (dish.getStock() < item.getQuantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient stock for dish: " + dish.getName()
                );
            }
            itemsTotal = itemsTotal.add(dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 3) Calculate total
        BigDecimal total = itemsTotal;
        if (total.compareTo(MINIMUM_CHARGE) < 0) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Below minimum charge");
        }

        // Process payment
        paymentService.processPayment(req.getOrderId(), itemsTotal, BigDecimal.ZERO);

        // 4) Persist order
        Order order = new Order();
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        // 5) Publish order confirmed event
        rabbitTemplate.convertAndSend(
                RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ROUTING_ORDER_CONFIRMED,
                order.getId()
        );

        return new OrderResponse(order.getId(), total, order.getStatus());
    }

    @Override
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(o -> new OrderResponse(o.getId(), o.getTotalAmount(), o.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public void cancelOrder(String orderId) {
        Order o = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        o.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(o);
    }

    @Override
    public OrderResponse getOrderDetails(String orderId) {
        Order o = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return new OrderResponse(o.getId(), o.getTotalAmount(), o.getStatus());
    }
}
