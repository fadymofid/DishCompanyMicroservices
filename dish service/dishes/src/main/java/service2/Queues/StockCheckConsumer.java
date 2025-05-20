package service2.Queues;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class StockCheckConsumer {
    private static final String STOCK_CHECK_QUEUE = "stock_check_queue";
    private static final String STOCK_CHECK_RESPONSE_QUEUE = "stock_check_response_queue";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dishes_service";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (com.rabbitmq.client.Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(STOCK_CHECK_QUEUE, false, false, false, null);
            channel.queueDeclare(STOCK_CHECK_RESPONSE_QUEUE, false, false, false, null);
            System.out.println(" [*] Waiting for stock check requests in '" + STOCK_CHECK_QUEUE + "'.");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                String correlationId = delivery.getProperties().getCorrelationId();
                System.out.println(" [x] Received stock check request: " + message);
                try {
                    JSONObject req = new JSONObject(message);
                    JSONArray items = req.getJSONArray("items");
                    boolean available = true;
                    List<JSONObject> unavailableItems = new ArrayList<>();
                    try (Connection dbConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            int dishId = item.getInt("dishId");
                            int quantity = item.getInt("quantity");
                            String sql = "SELECT stock_quantity FROM dishes WHERE dish_id = ?";
                            try (PreparedStatement stmt = dbConn.prepareStatement(sql)) {
                                stmt.setInt(1, dishId);
                                ResultSet rs = stmt.executeQuery();
                                if (rs.next()) {
                                    int stock = rs.getInt("stock_quantity");
                                    if (stock < quantity) {
                                        available = false;
                                        JSONObject unavailable = new JSONObject();
                                        unavailable.put("dishId", dishId);
                                        unavailable.put("requested", quantity);
                                        unavailable.put("available", stock);
                                        unavailableItems.add(unavailable);
                                    }
                                } else {
                                    available = false;
                                    JSONObject unavailable = new JSONObject();
                                    unavailable.put("dishId", dishId);
                                    unavailable.put("requested", quantity);
                                    unavailable.put("available", 0);
                                    unavailableItems.add(unavailable);
                                }
                            }
                        }
                    }
                    // Respond with only true or false
                    String respStr = Boolean.toString(available);
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                            .correlationId(correlationId)
                            .build();
                    channel.basicPublish("", STOCK_CHECK_RESPONSE_QUEUE, replyProps, respStr.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent stock check response: " + respStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            channel.basicConsume(STOCK_CHECK_QUEUE, true, deliverCallback, consumerTag -> {});
            // Keep the consumer running
            while (true) { Thread.sleep(1000); }
        }
    }
}
