package models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Order {
    private int orderId;
    private int userId;
    private double totalAmount;
    private Timestamp orderDate;
    private String userName; // Additional field for admin view
    
    // Default constructor
    public Order() {}
    
    // Constructor with all fields
    public Order(int orderId, int userId, double totalAmount, Timestamp orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }
    
    // Constructor without orderId (for placing new orders)
    public Order(int userId, double totalAmount, Timestamp orderDate) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }
    
    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Timestamp getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    // Helper method to get formatted date
    public String getFormattedDate() {
        if (orderDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return sdf.format(orderDate);
        }
        return "";
    }
    
    // Helper method to get formatted date (short)
    public String getShortFormattedDate() {
        if (orderDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.format(orderDate);
        }
        return "";
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", totalAmount=" + totalAmount +
                ", orderDate=" + orderDate +
                ", userName='" + userName + '\'' +
                '}';
    }
}