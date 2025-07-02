package GroceMartApp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AdminOrderPanel extends JPanel {
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private JTable orderItemsTable;
    private DefaultTableModel orderItemsTableModel;
    private JLabel totalOrdersLabel;
    private JLabel totalRevenueLabel;
    
    // Database connection details
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "Mohan";
    private static final String DB_PASSWORD = "Mohan123";
    
    public AdminOrderPanel() {
        initializeUI();
        loadOrders();
        loadStatistics();
    }
    
    // Add this method for refreshing data (required by AdminDashboard)
    public void refreshData() {
        // Clear existing data
        ordersTableModel.setRowCount(0);
        orderItemsTableModel.setRowCount(0);
        
        // Reload all data
        loadOrders();
        loadStatistics();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        
        // Orders panel (top)
        JPanel ordersPanel = createOrdersPanel();
        splitPane.setTopComponent(ordersPanel);
        
        // Order items panel (bottom)
        JPanel orderItemsPanel = createOrderItemsPanel();
        splitPane.setBottomComponent(orderItemsPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Statistics panel
        JPanel statsPanel = createStatisticsPanel();
        add(statsPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 139, 34));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Admin Dashboard - Order Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(34, 139, 34));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(70, 130, 180));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.addActionListener(e -> {
            loadOrders();
            loadStatistics();
            orderItemsTableModel.setRowCount(0);
        });
        
        buttonPanel.add(refreshBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("All Orders"));
        
        // Orders table
        String[] orderColumns = {"Order ID", "Customer", "Total Amount", "Order Date", "Items Count"};
        ordersTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.getTableHeader().setBackground(new Color(34, 139, 34));
        ordersTable.getTableHeader().setForeground(Color.WHITE);
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add selection listener to load order items
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int orderId = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
                    loadOrderItems(orderId);
                }
            }
        });
        
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        panel.add(ordersScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOrderItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Order Items (Select an order above)"));
        
        // Order items table
        String[] itemColumns = {"Product Name", "Quantity", "Unit Price", "Subtotal"};
        orderItemsTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        orderItemsTable = new JTable(orderItemsTableModel);
        orderItemsTable.getTableHeader().setBackground(new Color(70, 130, 180));
        orderItemsTable.getTableHeader().setForeground(Color.WHITE);
        orderItemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane itemsScrollPane = new JScrollPane(orderItemsTable);
        panel.add(itemsScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        
        totalOrdersLabel = new JLabel("Total Orders: 0");
        totalOrdersLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalOrdersLabel.setForeground(new Color(34, 139, 34));
        
        totalRevenueLabel = new JLabel("Total Revenue: ₹0.00");
        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalRevenueLabel.setForeground(new Color(34, 139, 34));
        
        panel.add(totalOrdersLabel);
        panel.add(Box.createHorizontalStrut(50));
        panel.add(totalRevenueLabel);
        
        return panel;
    }
    
    private void loadOrders() {
        ordersTableModel.setRowCount(0);
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT o.order_id, u.name, o.total_amount, o.order_date, " +
                        "COUNT(oi.product_id) as item_count " +
                        "FROM orders o " +
                        "JOIN users u ON o.user_id = u.id " +
                        "LEFT JOIN order_items oi ON o.order_id = oi.order_id " +
                        "GROUP BY o.order_id, u.name, o.total_amount, o.order_date " +
                        "ORDER BY o.order_date DESC";
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getString("name"),
                    "₹" + String.format("%.2f", rs.getDouble("total_amount")),
                    dateFormat.format(rs.getTimestamp("order_date")),
                    rs.getInt("item_count")
                };
                ordersTableModel.addRow(row);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }
    
    private void loadOrderItems(int orderId) {
        orderItemsTableModel.setRowCount(0);
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT p.name, oi.quantity, oi.price, (oi.quantity * oi.price) as subtotal " +
                        "FROM order_items oi " +
                        "JOIN products p ON oi.product_id = p.id " +
                        "WHERE oi.order_id = ?";
            
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    "₹" + String.format("%.2f", rs.getDouble("price")),
                    "₹" + String.format("%.2f", rs.getDouble("subtotal"))
                };
                orderItemsTableModel.addRow(row);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading order items: " + e.getMessage());
        }
    }
    
    private void loadStatistics() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Total orders count
            String orderCountSql = "SELECT COUNT(*) FROM orders";
            Statement stmt1 = connection.createStatement();
            ResultSet rs1 = stmt1.executeQuery(orderCountSql);
            
            int totalOrders = 0;
            if (rs1.next()) {
                totalOrders = rs1.getInt(1);
            }
            
            // Total revenue
            String revenueSql = "SELECT SUM(total_amount) FROM orders";
            Statement stmt2 = connection.createStatement();
            ResultSet rs2 = stmt2.executeQuery(revenueSql);
            
            double totalRevenue = 0.0;
            if (rs2.next()) {
                totalRevenue = rs2.getDouble(1);
            }
            
            // Update labels
            totalOrdersLabel.setText("Total Orders: " + totalOrders);
            totalRevenueLabel.setText("Total Revenue: ₹" + String.format("%.2f", totalRevenue));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}