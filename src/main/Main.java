package main;

import GroceMartApp.ui.LoginPage;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }        
        // Create and display the GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}