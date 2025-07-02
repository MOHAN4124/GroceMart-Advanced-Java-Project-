package GroceMartApp.db;

import models.CartItem;
import models.Product;

import java.sql.*;
import java.util.*;

public class CartDAO {

    public static List<CartItem> getCartItemsByUserId(int userId) {
        List<CartItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT p.id, p.name, p.price, p.quantity AS stock_quantity, p.image, p.category, c.quantity AS cart_quantity " +
                         "FROM cart c JOIN products p ON c.product_id = p.id " +
                         "WHERE c.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Create Product object
                Product product = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock_quantity"),
                    rs.getString("image"),
                    rs.getString("category")
                );

                // Create CartItem with Product and cart_quantity
                CartItem item = new CartItem(product, rs.getInt("cart_quantity"));
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public static void addToCart(int userId, int productId, int quantity) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "MERGE INTO cart USING dual ON (user_id=? AND product_id=?) " +
                         "WHEN MATCHED THEN UPDATE SET quantity = quantity + ? " +
                         "WHEN NOT MATCHED THEN INSERT (user_id, product_id, quantity) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.setInt(4, userId);
            stmt.setInt(5, productId);
            stmt.setInt(6, quantity);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void updateCartItem(int userId, int productId, int quantity) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quantity);
            stmt.setInt(2, userId);
            stmt.setInt(3, productId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeCartItem(int userId, int productId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearCart(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM cart WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
