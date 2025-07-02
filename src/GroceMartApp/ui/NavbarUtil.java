package GroceMartApp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NavbarUtil {
    
    public static JPanel createSideNavbar(String userRole, ActionListener actionListener) {
        JPanel navbar = new JPanel();
        navbar.setLayout(new BoxLayout(navbar, BoxLayout.Y_AXIS));
        navbar.setBackground(new Color(52, 73, 94));
        navbar.setPreferredSize(new Dimension(200, 600));
        navbar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        // Logo/Title
        JLabel titleLabel = new JLabel("GroceMart");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        navbar.add(titleLabel);
        navbar.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Navigation buttons based on user role
        if ("admin".equalsIgnoreCase(userRole)) {
            addNavButton(navbar, "Dashboard", "dashboard", actionListener);
            addNavButton(navbar, "Products", "products", actionListener);
            addNavButton(navbar, "Orders", "orders", actionListener);
            addNavButton(navbar, "Users", "users", actionListener);
        } else {
            addNavButton(navbar, "Home", "home", actionListener);
            addNavButton(navbar, "Shop", "shop", actionListener);
            addNavButton(navbar, "Cart", "cart", actionListener);
            addNavButton(navbar, "Orders", "myorders", actionListener);
            addNavButton(navbar, "Profile", "profile", actionListener);
        }
        
        // Add some space before logout
        navbar.add(Box.createVerticalGlue());
        addNavButton(navbar, "Logout", "logout", actionListener);
        
        return navbar;
    }
    
    private static void addNavButton(JPanel navbar, String text, String actionCommand, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setActionCommand(actionCommand);
        button.addActionListener(actionListener);
        
        // Style the button
        button.setMaximumSize(new Dimension(170, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
        });
        
        navbar.add(button);
        navbar.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    public static JPanel createTopNavbar(String userName, ActionListener actionListener) {
        JPanel topNavbar = new JPanel(new BorderLayout());
        topNavbar.setBackground(new Color(44, 62, 80));
        topNavbar.setPreferredSize(new Dimension(0, 60));
        topNavbar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + userName);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Right side panel with user info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setActionCommand("logout");
        logoutBtn.addActionListener(actionListener);
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        rightPanel.add(logoutBtn);
        
        topNavbar.add(welcomeLabel, BorderLayout.WEST);
        topNavbar.add(rightPanel, BorderLayout.EAST);
        
        return topNavbar;
    }
    
    public static JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setForeground(new Color(44, 62, 80));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            panel.add(titleLabel, BorderLayout.NORTH);
        }
        
        return panel;
    }
    
    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        Color originalColor = backgroundColor;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    
    public static JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }
    
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(new Color(44, 62, 80));
        return label;
    }
}