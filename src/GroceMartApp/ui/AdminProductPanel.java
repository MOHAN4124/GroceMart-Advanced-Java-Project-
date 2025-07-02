package GroceMartApp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminProductPanel extends JPanel {
    private JTable productTable;
    private static AdminProductPanel currentInstance;

    private DefaultTableModel tableModel;
    private JTextField nameField, priceField, quantityField, categoryField, imageField;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private int selectedProductId = -1;
    
    // Database connection details
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "Mohan";
    private static final String DB_PASSWORD = "Mohan123";
    
    public AdminProductPanel() {
    	 currentInstance = this; // Add this line
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadProducts();
    }
    
    public static AdminProductPanel getCurrentInstance() {
        return currentInstance;
    }
    
    private void initializeComponents() {
        // Create table
        String[] columnNames = {"ID", "Name", "Price", "Quantity", "Category", "Image"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        productTable.getTableHeader().setBackground(new Color(45, 52, 54)); // Dark background like AdminDashboard
        productTable.getTableHeader().setForeground(new Color(255, 255, 255)); // White text
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold font
        productTable.getTableHeader().setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(116, 185, 255)), // Blue bottom border
            BorderFactory.createEmptyBorder(10, 15, 10, 15) // Padding
        		));
        
        productTable.setBackground(new Color(52, 73, 94)); // Slightly lighter background for rows
        productTable.setForeground(new Color(255, 255, 255)); // White text for data
        productTable.setSelectionBackground(new Color(116, 185, 255)); // Blue selection
        productTable.setSelectionForeground(new Color(255, 255, 255)); // White text when selected
        productTable.setGridColor(new Color(127, 140, 141)); // Subtle grid lines
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Clean font for data
        productTable.setRowHeight(25); // Better row height

     // Apply custom renderer to all columns for row coloring
     QuantityBasedRowRenderer renderer = new QuantityBasedRowRenderer();
     for (int i = 0; i < productTable.getColumnCount(); i++) {
         productTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
     }
        
        // Create form fields
        nameField = new JTextField(20);
        priceField = new JTextField(20);
        quantityField = new JTextField(20);
        categoryField = new JTextField(20);
        imageField = new JTextField(20);
        
        // Create buttons
        addButton = new JButton("Add Product");
        updateButton = new JButton("Update Product");
        deleteButton = new JButton("Delete Product");
        clearButton = new JButton("Clear Form");
        
        // Style buttons
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.WHITE);
        updateButton.setBackground(new Color(70, 130, 180));
        updateButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(128, 128, 128));
        clearButton.setForeground(Color.WHITE);
        
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
   
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Product List"));
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Product Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Price ($):"), gbc);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        formPanel.add(categoryField, gbc);
        
        // Image URL
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Image URL:"), gbc);
        gbc.gridx = 1;
        formPanel.add(imageField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        // Combine form and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(tablePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Table selection listener
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadProductToForm(selectedRow);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                } else {
                    clearForm();
                    updateButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });
        
        // Button listeners
        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearForm());
    }
    
    public void refreshFromExternal() {
        SwingUtilities.invokeLater(() -> {
            refreshData();
        });
    }
    
 // Custom renderer for row coloring based on quantity
    private class QuantityBasedRowRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                     boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                // Get quantity from the table model (column 3 is quantity)
                Object quantityObj = table.getModel().getValueAt(row, 3);
                if (quantityObj != null) {
                    int quantity = (Integer) quantityObj;
                    
                    if (quantity <= 5) {
                        c.setBackground(new Color(220, 20, 60)); // Red for quantity <= 5
                        c.setForeground(Color.WHITE);
                    } else if (quantity < 10) {
                        c.setBackground(new Color(255, 140, 0)); // Orange for quantity < 10
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(52, 73, 94)); // Normal background
                        c.setForeground(new Color(255, 255, 255));
                    }
                }
            } else {
                // Keep selection colors as they are
                c.setBackground(new Color(116, 185, 255));
                c.setForeground(new Color(255, 255, 255));
            }
            
            return c;
        }
    }
    
    private void loadProducts() {
        tableModel.setRowCount(0); // Clear existing data
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, name, price, quantity, category, image FROM products ORDER BY id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("category"),
                    rs.getString("image")
                };
                tableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void loadProductToForm(int row) {
        selectedProductId = (Integer) tableModel.getValueAt(row, 0);
        nameField.setText((String) tableModel.getValueAt(row, 1));
        priceField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        quantityField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        categoryField.setText((String) tableModel.getValueAt(row, 4));
        imageField.setText((String) tableModel.getValueAt(row, 5));
    }
    
    private void addProduct() {
        if (!validateForm()) return;
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO products (name, price, quantity, category, image) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, nameField.getText().trim());
            pstmt.setDouble(2, Double.parseDouble(priceField.getText().trim()));
            pstmt.setInt(3, Integer.parseInt(quantityField.getText().trim()));
            pstmt.setString(4, categoryField.getText().trim());
            pstmt.setString(5, imageField.getText().trim());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
                clearForm();
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and quantity", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateProduct() {
        if (selectedProductId == -1 || !validateForm()) return;
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE products SET name = ?, price = ?, quantity = ?, category = ?, image = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, nameField.getText().trim());
            pstmt.setDouble(2, Double.parseDouble(priceField.getText().trim()));
            pstmt.setInt(3, Integer.parseInt(quantityField.getText().trim()));
            pstmt.setString(4, categoryField.getText().trim());
            pstmt.setString(5, imageField.getText().trim());
            pstmt.setInt(6, selectedProductId);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
                clearForm();
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating product: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and quantity", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteProduct() {
        if (selectedProductId == -1) return;
        
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this product?",
            "Delete Confirmation",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "DELETE FROM products WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, selectedProductId);
                
                int result = pstmt.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                    clearForm();
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting product: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty() ||
            priceField.getText().trim().isEmpty() ||
            quantityField.getText().trim().isEmpty() ||
            categoryField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            
            if (price < 0 || quantity < 0) {
                JOptionPane.showMessageDialog(this, "Price and quantity must be non-negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
        categoryField.setText("");
        imageField.setText("");
        selectedProductId = -1;
        productTable.clearSelection();
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    public void refreshData() {
        loadProducts();
        clearForm();
    }
}