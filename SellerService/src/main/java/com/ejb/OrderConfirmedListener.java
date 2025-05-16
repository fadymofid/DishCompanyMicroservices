package com.ejb;

import com.DAO.OrderItemDAO;
import com.DAO.DishDAO;
import com.models.OrderItem;
import com.models.Dish;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;


@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName = "destination",     propertyValue = "order.confirmed.queue")
        }
)
public class OrderConfirmedListener implements MessageListener {

    @EJB
    private DishDAO dishDAO;

    @EJB
    private OrderItemDAO orderItemDAO;

    @Override
    public void onMessage(Message message) {
        try {
            String text = ((TextMessage) message).getText();
            Long orderId = Long.parseLong(text);
            for (OrderItem item : orderItemDAO.findByOrderId(orderId)) {
                Dish d = dishDAO.find(item.getDish().getId());
                d.setStock(d.getStock() - item.getQuantity());
                dishDAO.update(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

