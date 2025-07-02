package GroceMartApp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HomePageFrame extends JFrame {
    private Connection connection;
    private int currentUserId;
    private String currentUserName;
    private JPanel productsPanel;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private List<Product> products;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private CartPage cartPage;
    private OrderPage orderPage;
    
    // Database connection details
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "Mohan";
    private static final String DB_PASSWORD = "Mohan123";
    
 // Replace the existing constructor with this updated version
    public HomePageFrame(int userId, String userName, Connection conn) {
        this.currentUserId = userId;
        this.currentUserName = userName;
        this.products = new ArrayList<>();
        
        // Set static instance reference
        currentHomePageInstance = this;
        
        // Initialize database connection
        try {
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!");
        }
        
        initializeUI();
        loadProducts();
        
        setTitle("GroceMart - Welcome " + userName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
    
    private void initializeUI() {
        // Use CardLayout to switch between home, cart, and orders
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create home panel
        JPanel homePanel = createHomePanel();
        
        // Create cart page
        cartPage = new CartPage(this, connection, currentUserId);
        
        // Create order page
        orderPage = new OrderPage(this, connection, currentUserId);
        
        // Add panels to card layout
        mainPanel.add(homePanel, "HOME");
        mainPanel.add(cartPage, "CART");
        mainPanel.add(orderPage, "ORDERS");
        
        add(mainPanel);
        
        // Show home panel initially
        cardLayout.show(mainPanel, "HOME");
    }    
    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        homePanel.add(headerPanel, BorderLayout.NORTH);
        
        // Search and filter panel
        JPanel searchPanel = createSearchPanel();
        
        // Products panel (scroll pane)
        productsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        productsPanel.setBackground(Color.WHITE);
        productsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Create main content panel
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.add(searchPanel, BorderLayout.NORTH);
        mainContent.add(scrollPane, BorderLayout.CENTER);
        
        homePanel.add(mainContent, BorderLayout.CENTER);
        
        return homePanel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 139, 34));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("GroceMart - Fresh Groceries");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(34, 139, 34));
        
        // Order button
        JButton orderBtn = new JButton("My Orders");
        orderBtn.setBackground(new Color(75, 0, 130)); // Indigo color
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setFont(new Font("Arial", Font.BOLD, 14));
        orderBtn.addActionListener(e -> navigateToCard("ORDERS"));
        
        JButton cartBtn = new JButton("Cart");
        cartBtn.setBackground(new Color(255, 165, 0));
        cartBtn.setForeground(Color.WHITE);
        cartBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cartBtn.addActionListener(e -> navigateToCard("CART"));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 20, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.addActionListener(e -> logout());
        
        // Add buttons in order
        buttonPanel.add(orderBtn);
        buttonPanel.add(cartBtn);
        buttonPanel.add(logoutBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBackground(new Color(240, 248, 255));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        
        JLabel categoryLabel = new JLabel("Category:");
        categoryCombo = new JComboBox<>(new String[]{"All", "Fruits", "Vegetables", "Dairy", "Bakery", "Meat", "Beverages"});
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(70, 130, 180));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> filterProducts());
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.setBackground(new Color(128, 128, 128));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(e -> clearSearch());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(categoryLabel);
        searchPanel.add(categoryCombo);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        
        return searchPanel;
    }
    
    
    
    private void loadProducts() {
        products.clear();
        try {
            String sql = "SELECT * FROM products WHERE quantity > 0 ORDER BY name";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("image"),
                    rs.getString("category")
                );
                products.add(product);
            }
            
            displayProducts(products);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void displayProducts(List<Product> productsToShow) {
        productsPanel.removeAll();
        
        for (Product product : productsToShow) {
            JPanel productCard = createProductCard(product);
            productsPanel.add(productCard);
        }
        
        productsPanel.revalidate();
        productsPanel.repaint();
    }
    
    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(250, 300));
        
        // Product image - Enhanced image loading
        JLabel imageLabel = new JLabel("No Image", JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 150));
        imageLabel.setBackground(new Color(240, 240, 240));
        imageLabel.setOpaque(true);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        imageLabel.setForeground(Color.GRAY);
        
        // Load actual image if available
        if (product.getImage() != null && !product.getImage().trim().isEmpty()) {
            ImageIcon imageIcon = loadProductImage(product.getImage(), product.getName());
            if (imageIcon != null) {
                imageLabel.setIcon(imageIcon);
                imageLabel.setText(""); // Remove "No Image" text
            }
        }
        
        // Product info panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel("<html><b>" + product.getName() + "</b></html>");
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        
        JLabel priceLabel = new JLabel("₹" + String.format("%.2f", product.getPrice()));
        priceLabel.setHorizontalAlignment(JLabel.CENTER);
        priceLabel.setForeground(new Color(34, 139, 34));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel stockLabel = new JLabel("Stock: " + product.getQuantity());
        stockLabel.setHorizontalAlignment(JLabel.CENTER);
        stockLabel.setForeground(Color.GRAY);
        
        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setBackground(new Color(34, 139, 34));
        addToCartBtn.setForeground(Color.WHITE);
        addToCartBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addToCartBtn.addActionListener(e -> addToCart(product));
        
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(stockLabel);
        infoPanel.add(addToCartBtn);
        
        card.add(imageLabel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private ImageIcon loadProductImage(String imagePath, String productName) {
        try {
            // First, try to load from the exact path
            java.io.File imageFile = new java.io.File(imagePath);
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                if (originalIcon.getIconWidth() > 0) {
                    Image scaledImage = originalIcon.getImage().getScaledInstance(
                        200, 150, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImage);
                }
            }
            
            // If exact path doesn't work, try to find in common image directories
            String[] possiblePaths = {
                "images/" + imagePath,
                "src/images/" + imagePath,
                "resources/images/" + imagePath,
                System.getProperty("user.dir") + "/images/" + imagePath,
                System.getProperty("user.dir") + "/" + imagePath
            };
            
            for (String path : possiblePaths) {
                imageFile = new java.io.File(path);
                if (imageFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(path);
                    if (originalIcon.getIconWidth() > 0) {
                        Image scaledImage = originalIcon.getImage().getScaledInstance(
                            200, 150, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                }
            }
            
            // Try to load from classpath (if images are in resources folder)
            try {
                java.net.URL imageURL = getClass().getClassLoader().getResource("images/" + imagePath);
                if (imageURL != null) {
                    ImageIcon originalIcon = new ImageIcon(imageURL);
                    if (originalIcon.getIconWidth() > 0) {
                        Image scaledImage = originalIcon.getImage().getScaledInstance(
                            200, 150, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                }
            } catch (Exception e) {
                // Continue to next attempt
            }
            
            // If no image found, create a placeholder with product name initial
            return createPlaceholderImage(productName);
            
        } catch (Exception e) {
            System.out.println("Error loading image for " + productName + ": " + e.getMessage());
            return createPlaceholderImage(productName);
        }
    }

    private ImageIcon createPlaceholderImage(String productName) {
        // Create a simple placeholder image with the first letter of product name
        java.awt.image.BufferedImage placeholder = new java.awt.image.BufferedImage(200, 150, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = placeholder.createGraphics();
        
        // Set background color
        g2d.setColor(new Color(240, 248, 255));
        g2d.fillRect(0, 0, 200, 150);
        
        // Set text color and font
        g2d.setColor(new Color(70, 130, 180));
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        
        // Draw the first letter of product name
        String initial = productName.length() > 0 ? productName.substring(0, 1).toUpperCase() : "?";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (200 - fm.stringWidth(initial)) / 2;
        int y = (150 + fm.getAscent()) / 2;
        g2d.drawString(initial, x, y);
        
        g2d.dispose();
        return new ImageIcon(placeholder);
    }
    
    private void addToCart(Product product) {
        try {
            // First check if we have enough stock
            String stockCheckSql = "SELECT quantity FROM products WHERE id = ?";
            PreparedStatement stockCheckStmt = connection.prepareStatement(stockCheckSql);
            stockCheckStmt.setInt(1, product.getId());
            ResultSet stockRs = stockCheckStmt.executeQuery();
            
            int availableStock = 0;
            if (stockRs.next()) {
                availableStock = stockRs.getInt("quantity");
            }
            
            if (availableStock <= 0) {
                JOptionPane.showMessageDialog(this, "Sorry, " + product.getName() + " is out of stock!");
                return;
            }
            
            // Check if product already exists in cart
            String checkSql = "SELECT quantity FROM cart WHERE user_id = ? AND product_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, currentUserId);
            checkStmt.setInt(2, product.getId());
            
            ResultSet rs = checkStmt.executeQuery();
            
            int cartQuantity = 0;
            boolean productExistsInCart = false;
            
            if (rs.next()) {
                cartQuantity = rs.getInt("quantity");
                productExistsInCart = true;
            }
            
            // Check if adding one more would exceed available stock
            if (cartQuantity + 1 > availableStock) {
                JOptionPane.showMessageDialog(this, "Cannot add more. Only " + availableStock + " items available in stock!");
                return;
            }
            
            if (productExistsInCart) {
                // Update existing cart item
                String updateSql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setInt(1, cartQuantity + 1);
                updateStmt.setInt(2, currentUserId);
                updateStmt.setInt(3, product.getId());
                updateStmt.executeUpdate();
            } else {
                // Insert new cart item
                String insertSql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, 1)";
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                insertStmt.setInt(1, currentUserId);
                insertStmt.setInt(2, product.getId());
                insertStmt.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, product.getName() + " added to cart!");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding to cart!");
        }
    }
    
    private void filterProducts() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        
        List<Product> filteredProducts = new ArrayList<>();
        
        for (Product product : products) {
            boolean matchesSearch = searchText.isEmpty() || 
                                    product.getName().toLowerCase().contains(searchText);
            boolean matchesCategory = selectedCategory.equals("All") || 
                                     product.getCategory().equalsIgnoreCase(selectedCategory);
            
            if (matchesSearch && matchesCategory) {
                filteredProducts.add(product);
            }
        }
        
        displayProducts(filteredProducts);
    }
    
    private void clearSearch() {
        searchField.setText("");
        categoryCombo.setSelectedItem("All");
        displayProducts(products);
    }
    
    // This method will be called by CartPage to navigate back to home
    public void navigateToCard(String cardName) {
        if ("CART".equals(cardName)) {
            // Refresh cart data when navigating to cart
            cartPage.refreshCart();
        } else if ("ORDERS".equals(cardName)) {
            // Refresh order data when navigating to orders
            orderPage.refreshOrders();
        }
        cardLayout.show(mainPanel, cardName);
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.dispose();
            new LoginPage().setVisible(true);
        }
    }
    
 // Add this method to HomePageFrame.java class
    public void reduceProductStock(int productId, int quantityOrdered) {
        try {
            // First get current stock
            String getCurrentStockSql = "SELECT quantity FROM products WHERE id = ?";
            PreparedStatement getCurrentStockStmt = connection.prepareStatement(getCurrentStockSql);
            getCurrentStockStmt.setInt(1, productId);
            ResultSet rs = getCurrentStockStmt.executeQuery();
            
            if (rs.next()) {
                int currentStock = rs.getInt("quantity");
                int newStock = currentStock - quantityOrdered;
                
                // Make sure stock doesn't go below 0
                if (newStock < 0) {
                    newStock = 0;
                }
                
                // Update the stock in products table
                String updateStockSql = "UPDATE products SET quantity = ? WHERE id = ?";
                PreparedStatement updateStockStmt = connection.prepareStatement(updateStockSql);
                updateStockStmt.setInt(1, newStock);
                updateStockStmt.setInt(2, productId);
                updateStockStmt.executeUpdate();
                
                // Refresh the products display to show updated stock
                loadProducts();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating product stock: " + e.getMessage());
        }
    }

    // Add this method to get reference to HomePageFrame from other classes
    public static HomePageFrame getCurrentInstance() {
        return currentHomePageInstance;
    }

    // Add this static variable at the top of HomePageFrame class (with other instance variables)
    private static HomePageFrame currentHomePageInstance;
    
    public void placeOrderFromCart() {
        // This method can be called from CartPage when user places an order
        navigateToCard("ORDERS");
    }
    
    public void refreshOrderPage() {
        // Assuming you have an OrderPage instance in your card layout
        // You'll need to call the refresh method on your order page
        // This depends on how you've structured your card layout
        
        // If you have a reference to the OrderPage, call:
        if (orderPage != null) {
            orderPage.refreshOrders();
        }
    }
    
    // Inner Product class
    private static class Product {
        private int id;
        private String name;
        private double price;
        private int quantity;
        private String image;
        private String category;
        
        public Product(int id, String name, double price, int quantity, String image, String category) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.image = image;
            this.category = category;
        }
        
        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public String getImage() { return image; }
        public String getCategory() { return category; }
    }
}