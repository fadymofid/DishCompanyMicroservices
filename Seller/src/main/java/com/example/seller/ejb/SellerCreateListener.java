package com.example.seller.ejb;

import com.example.seller.DAO.SellerDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.seller.models.Seller;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import com.example.seller.DAO.SellerDTO;
import java.nio.charset.StandardCharsets;

@Singleton
@Startup
public class SellerCreateListener {

    private static final String EXCHANGE_NAME = "admin-exchange";
    private static final String ROUTING_KEY = "seller.create";
    private static final String QUEUE_NAME = "seller.create.queue";

    @EJB
    private SellerDAO sellerDAO; // Inject SellerDAO

    @PostConstruct
    public void startListening() {
        new Thread(this::consume).start();
    }

    private void consume() {
        try {
            // Use centralized RabbitMQ connection utility
            Connection connection = RabbitMQUtil.getConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            System.out.println("[SellerCreateListener] Waiting for seller.create messages...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("[SellerCreateListener] Received: " + json);

                try {
                    // Parse JSON to SellerDTO
                    ObjectMapper mapper = new ObjectMapper();
                    SellerDTO sellerDTO = mapper.readValue(json, SellerDTO.class);

                    // Convert DTO to Seller entity
                    Seller seller = new Seller();
                    seller.setCompanyName(sellerDTO.getCompanyName());
                    seller.setUsername(sellerDTO.getUsername());
                    seller.setPassword(sellerDTO.getPassword()); // Assume password is pre-hashed

                    // Persist to database
                    sellerDAO.save(seller);
                    System.out.println("[SellerCreateListener] Saved seller: " + seller.getCompanyName());

                } catch (Exception e) {
                    e.printStackTrace();
                    // Log error to "log.exchange" (bonus requirement)
                }
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** DTO for incoming JSON messages */

}