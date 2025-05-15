package com.example.order_shipping_service.messaging;

import com.example.order_shipping_service.DTO.DishDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class DishResponseListener {

    private final ConcurrentHashMap<Long, DishDTO> dishResponseCache = new ConcurrentHashMap<>();

    @RabbitListener(queues = "dish.response.queue")
    public void handleDishResponse(DishDTO dish) {
        // Cache the dish response
        dishResponseCache.put(dish.getId(), dish);
    }

    public DishDTO getDishResponse(Long dishId) {
        return dishResponseCache.remove(dishId); // Remove after retrieving to avoid stale data
    }
}