
package service2.DTO;


import java.util.List;

public class StockUpdateRequest {
    private Long orderId;
    private List<DishQuantity> items;

    public StockUpdateRequest() {}

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<DishQuantity> getItems() {
        return items;
    }

    public void setItems(List<DishQuantity> items) {
        this.items = items;
    }
}