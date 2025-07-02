package GroceMartApp.ui;

import GroceMartApp.db.OrderDAO;
import models.Order;
import models.OrderItem;
import session.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.sun.jdi.connect.spi.Connection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.List;


public class OrderPage extends JPanel {
    private JPanel mainPanel;
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private JTable orderItemsTable;
    private DefaultTableModel orderItemsTableModel;
    private JLabel totalOrdersLabel;
    private JLabel totalAmountLabel;
    private JScrollPane orderItemsScrollPane;
    private DecimalFormat currencyFormat;
    private static java.sql.Connection connection;
    private static int currentUserId;
    private static HomePageFrame parentFrame;
    
    
    public OrderPage(HomePageFrame parent, java.sql.Connection connection2, int currentUserId) {
        this.parentFrame = parent;
        this.connection = connection2;
        this.currentUserId = currentUserId;
        
        currencyFormat = new DecimalFormat("₹#,##0.00");
        initializeComponents();
        setupUI();
        loadOrders();
    }

    public void refreshOrders() {
        // Reset the UI to show tables instead of "no orders" panel
        mainPanel.removeAll();
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Recreate the content panel with split pane
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        
        JPanel ordersSection = createOrdersSection();
        JPanel itemsSection = createOrderItemsSection();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ordersSection, itemsSection);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        splitPane.setBackground(Color.WHITE);
        
        contentPanel.add(splitPane, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(createSummaryPanel(), BorderLayout.SOUTH);
        
        // Now load the orders
        loadOrders();
        
        // Refresh the UI
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void initializeComponents() {
    	setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1200, 800));
        setMinimumSize(new Dimension(1000, 600));
        
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Orders table setup
        String[] orderColumns = {"Order ID", "Order Date", "Total Amount", "Status"};
        ordersTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.setRowHeight(35);
        ordersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        ordersTable.getTableHeader().setBackground(new Color(52, 152, 219));
        ordersTable.getTableHeader().setForeground(Color.WHITE);
        ordersTable.setGridColor(new Color(230, 230, 230));
        ordersTable.setSelectionBackground(new Color(52, 152, 219, 50));
        
        // Order items table setup
        String[] itemColumns = {"Product Name", "Quantity", "Unit Price", "Total Price"};
        orderItemsTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderItemsTable = new JTable(orderItemsTableModel);
        orderItemsTable.setRowHeight(30);
        orderItemsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        orderItemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        orderItemsTable.getTableHeader().setBackground(new Color(46, 204, 113));
        orderItemsTable.getTableHeader().setForeground(Color.WHITE);
        orderItemsTable.setGridColor(new Color(230, 230, 230));
        orderItemsTable.setSelectionBackground(new Color(46, 204, 113, 50));
        
        // Summary labels
        totalOrdersLabel = new JLabel("Total Orders: 0");
        totalOrdersLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalOrdersLabel.setForeground(new Color(52, 73, 94));
        
        totalAmountLabel = new JLabel("Total Spent: ₹0.00");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalAmountLabel.setForeground(new Color(231, 76, 60));
    }
    
    private void setupUI() {
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        
        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        
        // Orders Section
        JPanel ordersSection = createOrdersSection();
        
        // Order Items Section
        JPanel itemsSection = createOrderItemsSection();
        
        // Split the content into two sections
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ordersSection, itemsSection);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        splitPane.setBackground(Color.WHITE);
        
        contentPanel.add(splitPane, BorderLayout.CENTER);
        
        // Summary Panel
        JPanel summaryPanel = createSummaryPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Add table selection listener
        ordersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loadOrderItems();
            }
        });
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("My Orders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setIcon(new ImageIcon(createOrderIcon()));
        titleLabel.setIconTextGap(10);
        
        JButton backButton = new JButton("← Back to Home");
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            if (parentFrame != null) {
                parentFrame.navigateToCard("HOME");
                // Don't dispose, just hide or navigate back
                setVisible(false);
            } else {
//                dispose();
                // Fallback for when parentFrame is null
                try {
                    int userId = SessionManager.getUserId();
                    String userName = SessionManager.getUsername();
                    JOptionPane.showMessageDialog(this, "Navigating back to home page...");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error navigating to home page");
                }
            }
        });
        
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(46, 204, 113));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadOrders());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(44, 62, 80));
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createOrdersSection() {
        JPanel ordersSection = new JPanel(new BorderLayout());
        ordersSection.setBackground(Color.WHITE);
        ordersSection.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Order History",
            0, 0,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersScrollPane.setBorder(BorderFactory.createEmptyBorder());
        ordersScrollPane.getViewport().setBackground(Color.WHITE);
        
        ordersSection.add(ordersScrollPane, BorderLayout.CENTER);
        
        return ordersSection;
    }
    
    private JPanel createOrderItemsSection() {
        JPanel itemsSection = new JPanel(new BorderLayout());
        itemsSection.setBackground(Color.WHITE);
        itemsSection.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            "Order Details",
            0, 0,
            new Font("Arial", Font.BOLD, 14),
            new Color(46, 204, 113)
        ));
        
        orderItemsScrollPane = new JScrollPane(orderItemsTable);
        orderItemsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        orderItemsScrollPane.getViewport().setBackground(Color.WHITE);
        
        // Add instruction label
        JLabel instructionLabel = new JLabel("Select an order above to view details", JLabel.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel instructionPanel = new JPanel(new BorderLayout());
        instructionPanel.setBackground(Color.WHITE);
        instructionPanel.add(instructionLabel, BorderLayout.CENTER);
        
        itemsSection.add(instructionPanel, BorderLayout.NORTH);
        itemsSection.add(orderItemsScrollPane, BorderLayout.CENTER);
        
        return itemsSection;
    }
    
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        summaryPanel.setBackground(new Color(236, 240, 241));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(189, 195, 199)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        summaryPanel.add(totalOrdersLabel);
        summaryPanel.add(new JLabel(" | "));
        summaryPanel.add(totalAmountLabel);
        
        return summaryPanel;
    }
    
    private void loadOrders() {
        try {
            int userId = (currentUserId != 0) ? currentUserId : SessionManager.getUserId();
            if (userId == -1 || userId == 0) {
                JOptionPane.showMessageDialog(this, "Please login first!", "Error", JOptionPane.ERROR_MESSAGE);
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
//                dispose();
                return;
            }
            
            List<Order> orders = OrderDAO.getOrdersByUserId(userId);
            
            // Clear existing data
            ordersTableModel.setRowCount(0);
            orderItemsTableModel.setRowCount(0);
            
            double totalSpent = 0.0;
            
            for (Order order : orders) {
                Object[] rowData = {
                    "#" + order.getOrderId(),
                    order.getShortFormattedDate(),
                    currencyFormat.format(order.getTotalAmount()),
                    "Completed" // You can add order status field to your database if needed
                };
                ordersTableModel.addRow(rowData);
                totalSpent += order.getTotalAmount();
            }
            
            // Update summary
            totalOrdersLabel.setText("Total Orders: " + orders.size());
            totalAmountLabel.setText("Total Spent: " + currencyFormat.format(totalSpent));
            
            if (orders.isEmpty()) {
                showNoOrdersPanel();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadOrderItems() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        try {
            // Extract order ID from the selected row (remove the # prefix)
            String orderIdString = ordersTable.getValueAt(selectedRow, 0).toString();
            int orderId = Integer.parseInt(orderIdString.substring(1));
            
            List<OrderItem> orderItems = OrderDAO.getOrderItemsByOrderId(orderId);
            
            // Clear existing data
            orderItemsTableModel.setRowCount(0);
            
            for (OrderItem item : orderItems) {
                Object[] rowData = {
                    item.getProductName(),
                    item.getQuantity(),
                    currencyFormat.format(item.getPrice()),
                    currencyFormat.format(item.getTotalPrice())
                };
                orderItemsTableModel.addRow(rowData);
            }
            
            // Update border title with order ID
            JPanel itemsSection = (JPanel) orderItemsScrollPane.getParent();
            itemsSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
                "Order Details - Order #" + orderId,
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                new Color(46, 204, 113)
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading order details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showNoOrdersPanel() {
        JLabel noOrdersLabel = new JLabel("No orders found. Start shopping now!", JLabel.CENTER);
        noOrdersLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        noOrdersLabel.setForeground(Color.GRAY);
        
        JPanel noOrdersPanel = new JPanel(new BorderLayout());
        noOrdersPanel.setBackground(Color.WHITE);
        noOrdersPanel.add(noOrdersLabel, BorderLayout.CENTER);
        
        JButton shopNowButton = new JButton("Start Shopping");
        shopNowButton.setFont(new Font("Arial", Font.BOLD, 14));
        shopNowButton.setBackground(new Color(52, 152, 219));
        shopNowButton.setForeground(Color.WHITE);
        shopNowButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        shopNowButton.setFocusPainted(false);
        shopNowButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        shopNowButton.addActionListener(e -> {
            if (parentFrame != null) {
                parentFrame.navigateToCard("HOME");
                setVisible(false);
            } else {
//                dispose();
                JOptionPane.showMessageDialog(this, "Navigating to shopping page...");
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(shopNowButton);
        
        noOrdersPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Replace the table with no orders panel
        mainPanel.removeAll();
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(noOrdersPanel, BorderLayout.CENTER);
        mainPanel.add(createSummaryPanel(), BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private Image createOrderIcon() {
        // Create a simple order icon
        int size = 20;
        Image img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(2, 2, size-4, size-4);
        g2d.setColor(new Color(52, 152, 219));
        g2d.drawRect(2, 2, size-4, size-4);
        g2d.drawLine(4, 6, size-4, 6);
        g2d.drawLine(4, 9, size-4, 9);
        g2d.drawLine(4, 12, size-4, 12);
        g2d.dispose();
        return img;
    }
    
    // Main method for testing
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            // For testing purposes, you might want to set a test user ID in SessionManager
            // SessionManager.createSession(1, "testuser", "USER"); // FIXED: Use createSession method
            new OrderPage(parentFrame, connection, currentUserId).setVisible(true);
        });
    }
}