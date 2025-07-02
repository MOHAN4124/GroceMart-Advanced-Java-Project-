package GroceMartApp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartPage extends JPanel {
    private HomePageFrame parentApp;
    private Connection connection;
    private int currentUserId;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private List<CartItem> cartItems;
    
    // Database connection constants - ADD THESE IF NOT ALREADY PRESENT
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "Mohan";
    private static final String DB_PASSWORD = "Mohan123";
    
    public CartPage(HomePageFrame parentApp, Connection connection, int currentUserId) {
        this.parentApp = parentApp;
        this.connection = connection;
        this.currentUserId = currentUserId;
        this.cartItems = new ArrayList<>();
        initializeUI();
        loadCartItems();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Cart table
        String[] columnNames = {"Product", "Price", "Quantity", "Subtotal", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only actions column is editable
            }
        };
        
        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(40);
        cartTable.getTableHeader().setBackground(new Color(34, 139, 34));
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add button renderer and editor for actions column
        cartTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        cartTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Cart Items"));
        
        // Bottom panel with total and checkout
        JPanel bottomPanel = createBottomPanel();
        
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 139, 34));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Shopping Cart");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JButton backBtn = new JButton("← Back to Home");
        backBtn.setBackground(new Color(70, 130, 180));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.addActionListener(e -> parentApp.navigateToCard("HOME"));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(new Color(240, 248, 255));
        
        totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(34, 139, 34));
        
        totalPanel.add(totalLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        JButton clearCartBtn = new JButton("Clear Cart");
        clearCartBtn.setBackground(new Color(220, 20, 60));
        clearCartBtn.setForeground(Color.WHITE);
        clearCartBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clearCartBtn.addActionListener(e -> clearCart());
        
        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setBackground(new Color(34, 139, 34));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 16));
        checkoutBtn.addActionListener(e -> checkout());
        
        buttonPanel.add(clearCartBtn);
        buttonPanel.add(checkoutBtn);
        
        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return bottomPanel;
    }
    
    private void loadCartItems() {
        cartItems.clear();
        tableModel.setRowCount(0);
        
        try {
            String sql = "SELECT c.product_id, c.quantity, p.name, p.price " +
                        "FROM cart c JOIN products p ON c.product_id = p.id " +
                        "WHERE c.user_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, currentUserId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                CartItem item = new CartItem(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                );
                cartItems.add(item);
                
                Object[] row = {
                    item.getProductName(),
                    "₹" + String.format("%.2f", item.getPrice()),
                    item.getQuantity(),
                    "₹" + String.format("%.2f", item.getSubtotal()),
                    "Remove"
                };
                tableModel.addRow(row);
            }
            
            updateTotal();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Public method to refresh cart data
    public void refreshCart() {
        loadCartItems();
    }
    
    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        totalLabel.setText("Total: ₹" + String.format("%.2f", total));
    }
    
    private void removeFromCart(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < cartItems.size()) {
            CartItem item = cartItems.get(rowIndex);
            
            try {
                String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setInt(1, currentUserId);
                stmt.setInt(2, item.getProductId());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    cartItems.remove(rowIndex);
                    tableModel.removeRow(rowIndex);
                    updateTotal();
                    JOptionPane.showMessageDialog(this, "Item removed from cart!");
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error removing item from cart!");
            }
        }
    }
    
    private void clearCart() {
        int option = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear your cart?", 
            "Clear Cart", 
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM cart WHERE user_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setInt(1, currentUserId);
                
                stmt.executeUpdate();
                cartItems.clear();
                tableModel.setRowCount(0);
                updateTotal();
                JOptionPane.showMessageDialog(this, "Cart cleared successfully!");
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error clearing cart!");
            }
        }
    }
    
    // ADD THIS NEW METHOD - Stock update functionality
    private void updateStockAfterOrder(int orderId) {
        try (Connection stockConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Get all order items for this order
            String sql = "SELECT product_id, quantity FROM order_items WHERE order_id = ?";
            PreparedStatement stmt = stockConnection.prepareStatement(sql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            // Update stock for each product in the order
            while (rs.next()) {
                int productId = rs.getInt("product_id");
                int quantityOrdered = rs.getInt("quantity");
                
                // Reduce stock in products table
                String updateStockSql = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
                PreparedStatement updateStmt = stockConnection.prepareStatement(updateStockSql);
                updateStmt.setInt(1, quantityOrdered);
                updateStmt.setInt(2, productId);
                updateStmt.setInt(3, quantityOrdered);
                int rowsUpdated = updateStmt.executeUpdate();
                
                // Optional: Log if stock couldn't be reduced (already at 0)
                if (rowsUpdated == 0) {
                    System.out.println("Warning: Could not reduce stock for product ID " + productId + 
                                     " - insufficient stock available");
                }
            }
            
            // Refresh HomePageFrame products if it exists
            if (parentApp != null) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        // Use reflection to call loadProducts if it exists
                        java.lang.reflect.Method loadProductsMethod = parentApp.getClass().getMethod("loadProducts");
                        loadProductsMethod.invoke(parentApp);
                    } catch (Exception e) {
                        System.out.println("Could not refresh home page products: " + e.getMessage());
                    }
                });
            }
            
            System.out.println("Stock updated successfully for order ID: " + orderId);
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating stock after order: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Warning: Stock levels may not be updated correctly.");
        }
    }
    
    // REPLACE YOUR EXISTING checkout() METHOD WITH THIS UPDATED VERSION
    private void checkout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }
        
        double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Total Amount: ₹" + String.format("%.2f", total) + "\n\nProceed with checkout?", 
            "Checkout Confirmation", 
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            try {
                connection.setAutoCommit(false);
                
                // Simply insert the order - let the trigger handle the ID generation
                String orderSql = "INSERT INTO orders (user_id, total_amount, order_date) VALUES (?, ?, SYSDATE)";
                PreparedStatement orderStmt = connection.prepareStatement(orderSql);
                orderStmt.setInt(1, currentUserId);
                orderStmt.setDouble(2, total);
                
                int rowsAffected = orderStmt.executeUpdate();
                System.out.println("Order inserted successfully");
                
                if (rowsAffected > 0) {
                    // Get the order ID that was just created by finding the latest order for this user
                    String getOrderIdSql = "SELECT order_id FROM orders WHERE user_id = ? AND order_date = (SELECT MAX(order_date) FROM orders WHERE user_id = ?)";
                    PreparedStatement getOrderStmt = connection.prepareStatement(getOrderIdSql);
                    getOrderStmt.setInt(1, currentUserId);
                    getOrderStmt.setInt(2, currentUserId);
                    
                    ResultSet orderResult = getOrderStmt.executeQuery();
                    int orderId = 0;
                    
                    if (orderResult.next()) {
                        orderId = orderResult.getInt("order_id");
                        System.out.println("Retrieved order ID: " + orderId);
                        
                        // Add order items
                        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                        PreparedStatement itemStmt = connection.prepareStatement(itemSql);
                        
                        for (CartItem item : cartItems) {
                            itemStmt.setInt(1, orderId);
                            itemStmt.setInt(2, item.getProductId());
                            itemStmt.setInt(3, item.getQuantity());
                            itemStmt.setDouble(4, item.getPrice());
                            itemStmt.addBatch();
                        }
                        
                        itemStmt.executeBatch();
                        System.out.println("Order items inserted successfully");
                        
                        // Clear cart
                        String clearCartSql = "DELETE FROM cart WHERE user_id = ?";
                        PreparedStatement clearStmt = connection.prepareStatement(clearCartSql);
                        clearStmt.setInt(1, currentUserId);
                        clearStmt.executeUpdate();
                        System.out.println("Cart cleared successfully");
                        
                        connection.commit();
                        
                        // **CRITICAL: Update stock after successful order placement**
                        updateStockAfterOrder(orderId);
                        
                        JOptionPane.showMessageDialog(this, "Order placed successfully!\nOrder ID: " + orderId);

                        // Reload cart (should be empty now)
                        loadCartItems();

                        // Refresh the order page if it exists
                        if (parentApp != null) {
                            parentApp.refreshOrderPage();
                        }
                    } else {
                        connection.rollback();
                        JOptionPane.showMessageDialog(this, "Error: Could not retrieve order ID!");
                    }
                }
                
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error processing order: " + e.getMessage());
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Button renderer class
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(new Color(220, 20, 60));
            setForeground(Color.WHITE);
            return this;
        }
    }
    
    // Button editor class
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            button.setBackground(new Color(220, 20, 60));
            button.setForeground(Color.WHITE);
            isPushed = true;
            currentRow = row;
            return button;
        }
        
        public Object getCellEditorValue() {
            if (isPushed) {
                removeFromCart(currentRow);
            }
            isPushed = false;
            return label;
        }
        
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
        
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
    
    // Inner CartItem class
    private static class CartItem {
        private int productId;
        private String productName;
        private double price;
        private int quantity;
        
        public CartItem(int productId, String productName, double price, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }
        
        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public double getSubtotal() { return price * quantity; }
    }
}