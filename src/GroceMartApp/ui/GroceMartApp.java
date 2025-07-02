package GroceMartApp.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class GroceMartApp extends JFrame {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DB_USERNAME = "Mohan";
    private static final String DB_PASSWORD = "Mohan123";
    
    private Connection connection;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Current user session
    private int currentUserId = -1;
    private String currentUserRole = "";
    private String currentUserName = "";
    
    public GroceMartApp() {
        initializeDatabase();
        initializeUI();
    }
    
    private void initializeDatabase() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }
    
    private void initializeUI() {
        setTitle("GroceMart - Online Grocery Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Initialize card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Add login panel as default
        showLoginPanel();
        
        add(mainPanel);
    }
    
    private void showLoginPanel() {
        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LOGIN");
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        JLabel titleLabel = new JLabel("Welcome to GroceMart");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(34, 139, 34));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(20, 0, 30, 0);
        panel.add(titleLabel, gbc);
        
        // Username field
        gbc.gridwidth = 1; gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        
        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(34, 139, 34));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(loginBtn, gbc);
        
        // Sign up button
        JButton signUpBtn = new JButton("Sign Up");
        signUpBtn.setBackground(new Color(70, 130, 180));
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 4; gbc.insets = new Insets(10, 10, 20, 10);
        panel.add(signUpBtn, gbc);
        
        // Event listeners
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (authenticateUser(username, password)) {
                if (currentUserRole.equals("admin")) {
                    showAdminPanel();
                } else {
                    showHomePage();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });
        
        signUpBtn.addActionListener(e -> showSignUpPage());
        
        return panel;
    }
    
    private boolean authenticateUser(String username, String password) {
        try {
            String sql = "SELECT id, role, name FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentUserId = rs.getInt("id");
                currentUserRole = rs.getString("role");
                currentUserName = rs.getString("name");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void showHomePage() {
        // Close current window and open HomePageFrame
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            HomePageFrame homeFrame = new HomePageFrame(currentUserId, currentUserName, connection);
            homeFrame.setVisible(true);
        });
    }
    
    public void showShopPage() {
        // This method can be removed or implemented as needed
        JOptionPane.showMessageDialog(this, "Shop page functionality to be implemented");
    }
    
    private void showSignUpPage() {
        // Close current window and open SignUpPageFrame
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            SignUpPageFrame signUpFrame = new SignUpPageFrame();
            signUpFrame.setVisible(true);
        });
    }
    
    private void showAdminPanel() {
        // Create admin panel in the current window
        AdminOrderPanel adminPanel = new AdminOrderPanel();
        mainPanel.add(adminPanel, "ADMIN");
        cardLayout.show(mainPanel, "ADMIN");
        
        // Add a back button or logout functionality for admin
        JPanel adminWrapper = new JPanel(new BorderLayout());
        
        // Create header with logout button
        JPanel adminHeader = new JPanel(new BorderLayout());
        adminHeader.setBackground(new Color(34, 139, 34));
        adminHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel adminTitle = new JLabel("Admin Panel");
        adminTitle.setFont(new Font("Arial", Font.BOLD, 20));
        adminTitle.setForeground(Color.WHITE);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 20, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.addActionListener(e -> logout());
        
        adminHeader.add(adminTitle, BorderLayout.WEST);
        adminHeader.add(logoutBtn, BorderLayout.EAST);
        
        adminWrapper.add(adminHeader, BorderLayout.NORTH);
        adminWrapper.add(adminPanel, BorderLayout.CENTER);
        
        mainPanel.removeAll();
        mainPanel.add(adminWrapper, "ADMIN");
        cardLayout.show(mainPanel, "ADMIN");
    }
    
    public void showCartPage() {
        // This method can be implemented when CartPage is created
        JOptionPane.showMessageDialog(this, "Cart functionality to be implemented");
    }
    
    public void navigateToCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }
    
    public void logout() {
        currentUserId = -1;
        currentUserRole = "";
        currentUserName = "";
        mainPanel.removeAll();
        showLoginPanel();
        revalidate();
        repaint();
    }
    
    public int getCurrentUserId() {
        return currentUserId;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    @Override
    public void dispose() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.dispose();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new GroceMartApp().setVisible(true);
        });
    }
}