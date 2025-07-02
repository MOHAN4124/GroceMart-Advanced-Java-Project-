package GroceMartApp.ui;

import GroceMartApp.ui.HomePageFrame;
import GroceMartApp.ui.SignUpPageFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPage extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    private JComboBox<String> roleComboBox;
    
    // Database connection details
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "Mohan";
    private static final String DB_PASSWORD = "Mohan123";
    
    public LoginPage() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setTitle("GroceMart - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign Up");
        
        String[] roles = {"User", "Admin"};
        roleComboBox = new JComboBox<>(roles);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("GroceMart Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(34, 139, 34));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Login as:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleComboBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton.setBackground(new Color(34, 139, 34));
        loginButton.setForeground(Color.WHITE);
        signUpButton.setBackground(new Color(70, 130, 180));
        signUpButton.setForeground(Color.WHITE);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);
        
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(this);
        signUpButton.addActionListener(this);
        
        // Enter key login
        passwordField.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton || e.getSource() == passwordField) {
            login();
        } else if (e.getSource() == signUpButton) {
            openSignUpPage();
        }
    }
    
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String selectedRole = (String) roleComboBox.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, name, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password); // In production, use hashed passwords
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                String name = rs.getString("name");
                String userRole = rs.getString("role");
                
                // Check if selected role matches user's actual role
                if (!userRole.equalsIgnoreCase(selectedRole)) {
                    JOptionPane.showMessageDialog(this, "Invalid role selection", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Successful login
                this.dispose();
                
                if ("Admin".equalsIgnoreCase(userRole)) {
                    // Create AdminDashboard with proper constructor
                    new AdminDashboard(userId, name).setVisible(true);
                } else {
                    // Create HomePage as JFrame instead of JPanel
                    new HomePageFrame(userId, name, conn).setVisible(true);
                }
                
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void openSignUpPage() {
        // Create SignUpPage as JFrame instead of JPanel
        new SignUpPageFrame().setVisible(true);
        this.dispose();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}