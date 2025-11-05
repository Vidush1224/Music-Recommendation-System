package main.java.dao;

import main.java.util.DatabaseConfig;
import main.java.model.Admin;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class AdminDAO {

    /**
     * Authenticate admin login
     * 
     * @param username Admin username
     * @param password Plain text password
     * @return Admin object if successful, null if failed
     */
    public Admin loginAdmin(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        // Trim password and hash it
        String hashedPassword = hashPassword(password.trim());

        // Debugging output
        System.out.println("Input Username: '" + username + "'");
        System.out.println("Input Password: '" + password + "'");
        System.out.println("Hashed Password: '" + hashedPassword + "'");
        System.out.println("Hash length: " + hashedPassword.length());

        String query = "SELECT * FROM admins WHERE admin_username = ? AND admin_password_hash = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, hashedPassword);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setAdminUsername(rs.getString("admin_username"));
                admin.setCreatedAt(rs.getTimestamp("created_at"));

                System.out.println("✓ Admin login successful! Welcome, " + admin.getAdminUsername());
                return admin;
            } else {
                System.err.println("❌ Invalid admin credentials!");
            }

        } catch (SQLException e) {
            System.err.println("Error during admin login: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create new admin (only for initial setup)
     * 
     * @param username Admin username
     * @param password Plain text password
     * @return Admin ID if successful, -1 if failed
     */
    public int createAdmin(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.length() < 8) {
            System.err.println("Invalid admin details! Password must be at least 8 characters.");
            return -1;
        }

        String hashedPassword = hashPassword(password);
        String query = "INSERT INTO admins (admin_username, admin_password_hash) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, hashedPassword);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int adminId = generatedKeys.getInt(1);
                    System.out.println("✓ Admin created successfully! ID: " + adminId);
                    return adminId;
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("❌ Admin username already exists!");
            } else {
                System.err.println("Error creating admin: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return -1;
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