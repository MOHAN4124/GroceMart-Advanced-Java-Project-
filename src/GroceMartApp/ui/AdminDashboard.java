package GroceMartApp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import GroceMartApp.ui.AdminOrderPanel;

public class AdminDashboard extends JFrame {
    private int adminId;
    private String adminName;
    private JTabbedPane tabbedPane;
    private AdminProductPanel productPanel;
    private AdminOrderPanel orderPanel;
    private JLabel welcomeLabel;
    private JLabel statsLabel;
    
    // Database connection details
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "Mohan";
    private static final String DB_PASSWORD = "Mohan123";
    
    public AdminDashboard(int adminId, String adminName) {
        this.adminId = adminId;
        this.adminName = adminName;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadDashboardStats();
        
        setTitle("GroceMart - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        productPanel = new AdminProductPanel();
        orderPanel = new AdminOrderPanel();
        
        welcomeLabel = new JLabel("Welcome, " + adminName + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(255, 255, 255)); // White text on dark background
        
        statsLabel = new JLabel("Loading dashboard statistics...");
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statsLabel.setForeground(new Color(189, 195, 199)); // Light gray text
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
     // Header panel with gradient-like modern background
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 52, 54)); // Dark modern background
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(116, 185, 255)),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setBackground(new Color(45, 52, 54));
        welcomePanel.add(welcomeLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(45, 52, 54));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60)); // Modern red
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(52, 152, 219)); // Modern blue
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshDashboard());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        
        headerPanel.add(welcomePanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
     // Stats panel with modern card-like appearance
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(new Color(52, 73, 94)); // Slightly different shade
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(127, 140, 141)),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        statsPanel.add(statsLabel);
        
        // Tabbed pane
        tabbedPane.addTab("Product Management", productPanel);
        tabbedPane.addTab("Order Management", orderPanel);
        tabbedPane.addTab("Dashboard Overview", createOverviewPanel());
        
        // Add components to frame
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createOverviewPanel() {
        JPanel overviewPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        overviewPanel.setBackground(new Color(52, 73, 94)); // Dark background
        overviewPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create info cards with modern colors
        overviewPanel.add(createInfoCard("Total Products", "Loading...", new Color(46, 204, 113))); // Modern green
        overviewPanel.add(createInfoCard("Total Orders", "Loading...", new Color(52, 152, 219)));   // Modern blue  
        overviewPanel.add(createInfoCard("Total Users", "Loading...", new Color(230, 126, 34)));    // Modern orange
        overviewPanel.add(createInfoCard("Revenue", "Loading...", new Color(231, 76, 60)));         // Modern red
        
        return overviewPanel;
    }
    
    private JPanel createInfoCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(45, 52, 54)); // Dark modern background
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(127, 140, 141), 1), // Subtle border
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        
        // Add subtle shadow effect with layered border
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(127, 140, 141), 1),
                BorderFactory.createLineBorder(new Color(52, 73, 94), 2)
            ),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(189, 195, 199)); // Light gray text
        
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color); // Keep the accent color for values
        
        // Add icon space (optional enhancement)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(45, 52, 54));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        addCardHoverEffect(card);
        return card;
    }
    
    private void setupEventHandlers() {
        // Tab change listener to refresh data
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 0) {
                productPanel.refreshData();
            } else if (selectedIndex == 1) {
                orderPanel.refreshData();
            } else if (selectedIndex == 2) {
                loadDashboardStats();
            }
        });
    }
    
    private void loadDashboardStats() {
        SwingUtilities.invokeLater(() -> {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // Get product count
                String productSql = "SELECT COUNT(*) FROM products";
                PreparedStatement pstmt = conn.prepareStatement(productSql);
                ResultSet rs = pstmt.executeQuery();
                int productCount = rs.next() ? rs.getInt(1) : 0;
                
                // Get order count
                String orderSql = "SELECT COUNT(*) FROM orders";
                pstmt = conn.prepareStatement(orderSql);
                rs = pstmt.executeQuery();
                int orderCount = rs.next() ? rs.getInt(1) : 0;
                
                // Get user count
                String userSql = "SELECT COUNT(*) FROM users WHERE role = 'User'";
                pstmt = conn.prepareStatement(userSql);
                rs = pstmt.executeQuery();
                int userCount = rs.next() ? rs.getInt(1) : 0;
                
                // Get total revenue
                String revenueSql = "SELECT SUM(total_amount) FROM orders";
                pstmt = conn.prepareStatement(revenueSql);
                rs = pstmt.executeQuery();
                double totalRevenue = rs.next() ? rs.getDouble(1) : 0.0;
                
                // Update stats label
                statsLabel.setText(String.format(
                    "Dashboard Stats: %d Products | %d Orders | %d Users | Revenue: ₹%.2f",
                    productCount, orderCount, userCount, totalRevenue
                ));
                
                // Update overview panel cards
                updateOverviewCards(productCount, orderCount, userCount, totalRevenue);
                
            } catch (SQLException ex) {
                statsLabel.setText("Error loading dashboard statistics");
                ex.printStackTrace();
            }
        });
    }
    
    private void updateOverviewCards(int productCount, int orderCount, int userCount, double revenue) {
        JPanel overviewPanel = (JPanel) tabbedPane.getComponentAt(2);
        Component[] components = overviewPanel.getComponents();
        
        if (components.length >= 4) {
            updateCardValue((JPanel) components[0], String.valueOf(productCount));
            updateCardValue((JPanel) components[1], String.valueOf(orderCount));
            updateCardValue((JPanel) components[2], String.valueOf(userCount));
            updateCardValue((JPanel) components[3], String.format("₹%.2f", revenue));
        }
    }
    
    private void updateCardValue(JPanel card, String value) {
        Component[] components = card.getComponents();
        if (components.length >= 2 && components[1] instanceof JLabel) {
            ((JLabel) components[1]).setText(value);
        }
    }
    
    private void addCardHoverEffect(JPanel card) {
        Color originalBg = card.getBackground();
        Color hoverBg = new Color(52, 73, 94); // Lighter shade
        
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(hoverBg);
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(originalBg);
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
    
    private void refreshDashboard() {
        loadDashboardStats();
        productPanel.refreshData();
        orderPanel.refreshData();
        JOptionPane.showMessageDialog(this, "Dashboard refreshed successfully!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginPage().setVisible(true);
        }
    }
}
