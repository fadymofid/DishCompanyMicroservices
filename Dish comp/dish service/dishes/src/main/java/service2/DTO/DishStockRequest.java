package service2.DTO;

public class DishStockRequest {
    private int dishId;
    private int quantity;
    private int orderId;
    private String correlationId;  // Add this field for message correlation

    // Default constructor
    public DishStockRequest() {}

    // Getters and setters with consistent formatting
    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}