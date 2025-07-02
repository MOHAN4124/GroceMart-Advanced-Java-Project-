package models;

public class OrderItem {
    private int orderId;
    private int productId;
    private int quantity;
    private double price;
    private String productName; // Additional field from JOIN with products table
    
    // Default constructor
    public OrderItem() {}
    
    // Constructor with all fields
    public OrderItem(int orderId, int productId, int quantity, double price) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
    
    // Constructor with product name
    public OrderItem(int orderId, int productId, int quantity, double price, String productName) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.productName = productName;
    }
    
    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    // Helper method to calculate total for this order item
    public double getTotalPrice() {
        return quantity * price;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId=" + orderId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", productName='" + productName + '\'' +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}