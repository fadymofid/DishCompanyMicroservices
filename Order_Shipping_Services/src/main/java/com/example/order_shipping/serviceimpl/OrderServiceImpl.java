package com.example.order_shipping.serviceimpl;

import com.example.order_shipping.DTO.OrderItemRequest;
import com.example.order_shipping.DTO.OrderRequest;
import com.example.order_shipping.DTO.OrderResponse;
import com.example.order_shipping.Models.Order;
import com.example.order_shipping.Models.OrderItem;
import com.example.order_shipping.Repo.OrderRepository;
import com.example.order_shipping.service.OrderService;
import com.example.order_shipping.service.ShippingService;
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
public class OrderServiceImpl implements OrderService {
    private static final BigDecimal MINIMUM_CHARGE = new BigDecimal("20.00");

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ShippingService shippingService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            RabbitTemplate rabbitTemplate,
                            ShippingService shippingService) {
        this.orderRepository   = orderRepository;
        this.rabbitTemplate    = rabbitTemplate;
        this.shippingService   = shippingService;
    }

    @Override
    @Transactional
    public OrderResponse processOrder(OrderRequest req) {
        // 1. Validate shipping region
        try {
            shippingService.validateShippingRegion(
                    req.getShippingCompanyId(),
                    req.getCustomerRegion()
            );
        } catch (ResponseStatusException ex) {
            // shippingService already published "shipping.failed"
            throw ex;
        }

        // 2. Publish stock-check request
        rabbitTemplate.convertAndSend("order-exchange", "stock.check", req);

        // 3. Wait & check stock validation (stub)
        boolean stockOk = listenForStock(req.getOrderId());
        if (!stockOk) {
            String reason = "Insufficient stock for order " + req.getOrderId();
            rabbitTemplate.convertAndSend("order-exchange", "order.rejected", reason);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }

        // 4. Calculate totals
        BigDecimal itemsTotal = req.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shippingFee = shippingService.calculateShippingFee(
                req.getShippingCompanyId(), req.getCustomerRegion()
        );
        BigDecimal total = itemsTotal.add(shippingFee);

        // 5. Enforce minimum charge
        if (total.compareTo(MINIMUM_CHARGE) < 0) {
            String reason = String.format("Order total %s below minimum %s", total, MINIMUM_CHARGE);
            rabbitTemplate.convertAndSend("order-exchange", "order.rejected", reason);
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, reason);
        }

        // 6. Persist order and items
        Order order = new Order();
        order.setCustomerId(req.getCustomerId());
        order.setShippingCompanyId(req.getShippingCompanyId());
        order.setTotalAmount(total);
        List<OrderItem> items = req.getItems().stream().map(r -> {
            OrderItem oi = new OrderItem();
            oi.setDishId(r.getDishId());
            oi.setQuantity(r.getQuantity());
            oi.setPriceAtOrder(r.getPrice());
            oi.setOrder(order);
            return oi;
        }).collect(Collectors.toList());
        order.setItems(items);
        orderRepository.save(order);

        // 7. Publish confirmation
        rabbitTemplate.convertAndSend("order-exchange", "order.confirmed", order.getId());

        return new OrderResponse(order.getId(), order.getTotalAmount(), order.getStatus());
    }

    // Stub: in real code use a listener + CompletableFuture to await stock validation
    private boolean listenForStock(Long orderId) {
        return true;
    }
}
