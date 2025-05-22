package service2.Queues;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import service2.DTO.StockUpdateRequest;
import service2.DTO.DishQuantity;
import service2.Services.DishService;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.ejb.Startup;
import java.nio.charset.StandardCharsets;

@Singleton
@Startup
public class StockUpdateListener {

    private static final String STOCK_UPDATE_QUEUE = "stock_update_queue";

    private Connection connection;
    private Channel channel;

    @Inject
    private DishService dishService;

    @PostConstruct
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(STOCK_UPDATE_QUEUE, false, false, false, null);

            DeliverCallback callback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("[✓] Stock update received: " + message);

                try {
                    ObjectMapper mapper = new ObjectMapper();
                    StockUpdateRequest update = mapper.readValue(message, StockUpdateRequest.class);

                    for (DishQuantity item : update.getItems()) {
                        dishService.decrementStock(item.getDishId(), item.getQuantity());
                    }

                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (Exception e) {
                    System.err.println("[❌] Failed to process stock update: " + e.getMessage());
                    e.printStackTrace();
                }
            };

            channel.basicConsume(STOCK_UPDATE_QUEUE, false, callback, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
