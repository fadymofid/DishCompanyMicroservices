#java version
openjdk 17.0.14 2025-01-21
OpenJDK Runtime Environment Temurin-17.0.14+7 (build 17.0.14+7)
OpenJDK 64-Bit Server VM Temurin-17.0.14+7 (build 17.0.14+7, mixed mode, sharing)

#project structure
created 3 microservices (user_service (java springboot + MySQL) - dishes_service (EJB + MySQL)- orders&shipping_service (java springboot + MySQL))
front end is built using html,css and js only

#assumptions
1-minimum charge of an order should be 20$ excluding shipping fee
2-user enter their balance when registering their account

#how to run the project
1-create database with the name userservice in mysql 
2-run the user_service service by running the main function of the service and the tables will be created automatically
3-create database with the name orders_shipping_Services
4-run the orders&shipping_service_service service by running the main function of the service and the tables will be created automatically
5-create database with the name dishes_service using the following sql queries
CREATE TABLE dishes (
    dish_id INT AUTO_INCREMENT PRIMARY KEY,
    seller_id INT NOT NULL, 
    dish_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL
);
CREATE TABLE sold_dishes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dish_id INT NOT NULL,
    customer_id INT NOT NULL,
    FOREIGN KEY (dish_id) REFERENCES dishes(dish_id)
);
6-run the dishes_service service by running the command standalone.bat in the cmd then running mvn clean install in the terminal
7-run RabbitMQ server using the following commands
* cd $Env:RABBITMQ_SERVER\sbin
* .\rabbitmq-server.ba
8-run the main function in the file ordersAndShipmentService/notifications/CustomerNotificationConsumer to create CustomerNotificationConsumer queue 
9- run the main function in the file StockCheckConsumer in the dishes_service to create the StockCheckConsumer queue
