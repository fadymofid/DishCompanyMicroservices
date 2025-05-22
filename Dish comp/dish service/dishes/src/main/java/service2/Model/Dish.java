package service2.Model;

public class Dish {
    private int dishId;
    private int sellerId;
    private String dishName;
    private String description;
    private double price;
    private int stockQuantity;
   

   public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
      public Dish(int dishId, int sellerId, String dishName, String description, double price, int stockQuantity) {
         this.dishId = dishId;
         this.sellerId = sellerId;
         this.dishName = dishName;
         this.description = description;
         this.price = price;
         this.stockQuantity = stockQuantity;
      }
      public Dish() {
         // Default constructor
}
    // Getters and Setters
    public int getDishId() { return dishId; }
    public void setDishId(int dishId) { this.dishId = dishId; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }

 
}
