package com.example.seller.ejb;



import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQUtil {
    private static final String HOST = "localhost";
    private static final String USER = "guest";
    private static final String PASS = "guest";

    private static Connection connection;

    public static synchronized Connection getConnection() throws Exception {
        if (connection == null || !connection.isOpen()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setUsername(USER);
            factory.setPassword(PASS);
            connection = factory.newConnection();
        }
        return connection;
    }
}
