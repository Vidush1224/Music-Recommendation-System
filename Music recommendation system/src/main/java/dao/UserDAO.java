package main.java.dao;

import main.java.util.DatabaseConfig;
import main.java.model.User;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class UserDAO {

    /**
     * Register a new user
     * 
     * @param username Username
     * @param email    Email
     * @param password Plain text password (will be hashed)
     * @return User ID if successful, -1 if failed
     */
    public int registerUser(String username, String email, String password) {
        // Validate inputs
        if (username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.length() < 6) {
            System.err.println("Invalid registration details!");
            return -1;
        }

        String hashedPassword = hashPassword(password);
        String query = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, email.trim().toLowerCase());
            pstmt.setString(3, hashedPassword);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    System.out.println("✓ User registered successfully! ID: " + userId);
                    return userId;
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("❌ Username or email already exists!");
            } else {
                System.err.println("Error registering user: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return -1;
    }

    /**
     * Authenticate user login
     * 
     * @param username Username or email
     * @param password Plain text password
     * @return User object if successful, null if failed
     */
    public User loginUser(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        String hashedPassword = hashPassword(password);
        String query = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password_hash = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, username.trim().toLowerCase());
            pstmt.setString(3, hashedPassword);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at"));

                System.out.println("✓ Login successful! Welcome, " + user.getUsername());
                return user;
            } else {
                System.err.println("❌ Invalid username/email or password!");
            }

        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get user by ID
     * 
     * @param userId User ID
     * @return User object or null
     */
    public User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Check if username exists
     * 
     * @param username Username to check
     * @return true if exists
     */
    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username.trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }

        return false;
    }

    /**
     * Check if email exists
     * 
     * @param email Email to check
     * @return true if exists
     */
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email.trim().toLowerCase());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
        }

        return false;
    }

    /**
     * Update user profile
     * 
     * @param user User object with updated information
     * @return true if successful
     */
    public boolean updateUser(User user) {
        String query = "UPDATE users SET email = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getEmail());
            pstmt.setInt(2, user.getUserId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Change user password
     * 
     * @param userId      User ID
     * @param oldPassword Old password (plain text)
     * @param newPassword New password (plain text)
     * @return true if successful
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // First verify old password
        String verifyQuery = "SELECT password_hash FROM users WHERE user_id = ?";
        String updateQuery = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {

            // Verify old password
            try (PreparedStatement pstmt = conn.prepareStatement(verifyQuery)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String oldPasswordHash = hashPassword(oldPassword);

                    if (!storedHash.equals(oldPasswordHash)) {
                        System.err.println("❌ Old password is incorrect!");
                        return false;
                    }
                }
            }

            // Update to new password
            String newPasswordHash = hashPassword(newPassword);
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, newPasswordHash);
                pstmt.setInt(2, userId);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✓ Password changed successfully!");
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Hash password using SHA-256
     * 
     * @param password Plain text password
     * @return Hashed password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
