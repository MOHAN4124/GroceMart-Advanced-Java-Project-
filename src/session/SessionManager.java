package session;

public class SessionManager {
    private static int userId = -1;
    private static String username = "";
    private static String role = "";
    
    // Create session when user logs in
    public static void createSession(int id, String name, String userRole) {
        userId = id;
        username = name;
        role = userRole;
    }
    
    // Get current user ID
    public static int getUserId() {
        return userId;
    }
    
    // Get current username
    public static String getUsername() {
        return username;
    }
    
    // Get current user role
    public static String getRole() {
        return role;
    }
    
    // Check if user is logged in
    public static boolean isLoggedIn() {
        return userId != -1;
    }
    
    // Check if current user is admin
    public static boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
    
    // Clear session (logout)
    public static void clearSession() {
        userId = -1;
        username = "";
        role = "";
    }
    
    // Get session info as string
    public static String getSessionInfo() {
        if (isLoggedIn()) {
            return "Logged in as: " + username + " (" + role + ")";
        }
        return "Not logged in";
    }
}