package com.messaging;

import com.models.Dish;
import com.DAO.DishDAO;
import com.DTO.DishDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DishRequestListener {

    @Autowired
    private DishDAO dishDAO;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "dish.request.queue")
    public void handleDishRequest(Long dishId) {
        // Fetch the dish from the database
        Dish dish = dishDAO.find(dishId);
        if (dish != null) {
            // Convert to DTO and send the response
            DishDTO dishDTO = new DishDTO(dish.getId(), dish.getName(), dish.getPrice(), dish.getStock());
            rabbitTemplate.convertAndSend("dish-exchange", "dish.response", dishDTO);
        }
    }
}