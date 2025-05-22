package com.user_service.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user_service.dto.PaymentRequest;
import com.user_service.dto.PaymentResponse;
import com.user_service.entity.User;
import com.rabbitmq.client.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PaymentListener {

    private static final String REQ_QUEUE  = "payment_request_queue";
    private static final String RESP_QUEUE = "payment_response_queue";

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Connection connection;
    private Channel channel;

    @PostConstruct
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setPort(5672);

            connection = factory.newConnection();
            channel    = connection.createChannel();

            // Declare both request and response queues
            channel.queueDeclare(REQ_QUEUE,  false, false, false, null);
            channel.queueDeclare(RESP_QUEUE, false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String body        = new String(delivery.getBody(), StandardCharsets.UTF_8);
                AMQP.BasicProperties props = delivery.getProperties();
                String corrId      = props.getCorrelationId();

                try {
                    // 1) Deserialize request
                    PaymentRequest req = objectMapper.readValue(body, PaymentRequest.class);

                    // 2) Lookup user
                    User user = userService.getUserById(req.getCustomerId());
                    boolean success = false;
                    String failure  = null;
                    if (user == null) {
                        failure = "User not found";
                    } else if (user.getBalance() < req.getAmount()) {
                        failure = "Insufficient balance";
                    } else {

                        userService.saveUser(user);
                        success = true;
                    }

                    // 4) Build response
                    PaymentResponse resp = new PaymentResponse(corrId, success, failure);
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                            .correlationId(corrId)
                            .contentType("application/json")
                            .deliveryMode(2)
                            .build();

                    String respBody = objectMapper.writeValueAsString(resp);
                    channel.basicPublish(
                            "",
                            RESP_QUEUE,
                            replyProps,
                            respBody.getBytes(StandardCharsets.UTF_8)
                    );

                } catch (Exception ex) {
                    // log error
                    ex.printStackTrace();
                } finally {
                    // 5) ACK the request
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };

            // Start consuming with manual acks
            channel.basicConsume(REQ_QUEUE, false, deliverCallback, consumerTag -> {});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (channel   != null) channel.close();
            if (connection!= null) connection.close();
        } catch (Exception ignore) {}
    }
}
