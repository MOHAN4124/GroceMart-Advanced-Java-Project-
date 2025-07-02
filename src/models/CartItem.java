package models;

public class CartItem {
    private int userId;
    private int productId;
    private int quantity;
    private Product product; // Associated product object
    
    // Default constructor
    public CartItem() {}
    
    // Constructor with Product object and quantity
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.productId = product.getId();
        this.quantity = quantity;
    }
    
    // Constructor with all basic fields
    public CartItem(int userId, int productId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
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
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productId = product.getId();
        }
    }
    
    // Helper methods to get product details without null checks
    public String getProductName() {
        return product != null ? product.getName() : "";
    }
    
    public double getPrice() {
        return product != null ? product.getPrice() : 0.0;
    }
    
    public String getProductImage() {
        return product != null ? product.getImage() : "";
    }
    
    public String getProductCategory() {
        return product != null ? product.getCategory() : "";
    }
    
    // Calculate total price for this cart item
    public double getTotalPrice() {
        return getPrice() * quantity;
    }
    
    @Override
    public String toString() {
        return "CartItem{" +
                "userId=" + userId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", productName='" + getProductName() + '\'' +
                ", price=" + getPrice() +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}