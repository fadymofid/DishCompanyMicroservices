//package com.example.seller.ejb;
//
//import com.example.seller.DAO.DishDAO;
//import com.example.seller.DAO.SellerDAO;
//import com.example.seller.models.Dish;
//import com.example.seller.models.Seller;
//import jakarta.ejb.ActivationConfigProperty;
//import jakarta.ejb.EJB;
//import jakarta.ejb.MessageDriven;
//import jakarta.jms.JMSException;
//import jakarta.jms.Message;
//import jakarta.jms.MessageListener;
//import jakarta.jms.TextMessage;
//
///**
// * Listens for JMS messages published when an order is completed.
// * Expects a JSON payload like:
// *   { "orderId": 123, "dishId": 456, "quantity": 2, "customerUsername": "alice" }
// */
//@MessageDriven(
//        activationConfig = {
//                @ActivationConfigProperty(propertyName  = "destinationLookup",
//                        propertyValue = "java:/jms/queue/OrderCompletedQueue"),
//                @ActivationConfigProperty(propertyName  = "destinationType",
//                        propertyValue = "javax.jms.Queue"),
//                @ActivationConfigProperty(propertyName  = "acknowledgeMode",
//                        propertyValue = "Auto-acknowledge")
//        }
//)
//public class OrderEventListener implements MessageListener {
//
//    @EJB
//    private DishDAO dishDAO;
//
//    @EJB
//    private SellerDAO sellerDAO;
//
//    @Override
//    public void onMessage(Message message) {
//        try {
//            if (!(message instanceof TextMessage)) {
//                return;
//            }
//            String json = ((TextMessage) message).getText();
//            // Simple parsing (use a JSON library in real code!)
//            OrderEvent event = OrderEvent.fromJson(json);
//
//            // Fetch dish and seller
//            Dish dish = dishDAO.find(event.getDishId());
//            Seller seller = dish.getSeller();
//
//            // For demonstration, just decrement stock and log history
//            dish.setStock(dish.getStock() - event.getQuantity());
//            dishDAO.update(dish);
//
//            // In a real app you'd persist a SalesHistory record with customer & shipping info...
//
//            System.out.printf("OrderEventListener: recorded sale of %d×%s for seller %s%n",
//                    event.getQuantity(), dish.getName(), seller.getCompanyName());
//
//        } catch (JMSException e) {
//            throw new RuntimeException("Failed to process order event", e);
//        }
//    }
//
//    /** Simple internal DTO for parsing the JSON payload */
//    private static class OrderEvent {
//        private long orderId;
//        private long dishId;
//        private int  quantity;
//        private String customerUsername;
//
//        static OrderEvent fromJson(String json) {
//            // TODO: Replace with Jackson/Gson parsing
//            OrderEvent ev = new OrderEvent();
//            // naive parsing for illustration only:
//            ev.orderId          = Long.parseLong(getField(json, "orderId"));
//            ev.dishId           = Long.parseLong(getField(json, "dishId"));
//            ev.quantity         = Integer.parseInt(getField(json, "quantity"));
//            ev.customerUsername = getField(json, "customerUsername").replace("\"", "");
//            return ev;
//        }
//
//        private static String getField(String json, String name) {
//            String[] parts = json.split(name + "\":");
//            String rest  = parts[1];
//            return rest.contains(",")
//                    ? rest.substring(0, rest.indexOf(",")).trim()
//                    : rest.substring(0, rest.indexOf("}")).trim();
//        }
//
//        long getOrderId()          { return orderId; }
//        long getDishId()           { return dishId; }
//        int  getQuantity()         { return quantity; }
//        String getCustomerUsername(){ return customerUsername; }
//    }
//}
