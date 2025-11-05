package main.java.util;

import main.java.util.DatabaseConfig;
import main.java.dao.AdminDAO;

/**
 * Setup utility to create default admin account
 * Run this once during initial setup
 */
public class AdminSetup {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║     Admin Account Setup Utility              ║");
        System.out.println("╚═══════════════════════════════════════════════╝\n");

        // Test database connection first
        System.out.println("Testing database connection...");
        if (!DatabaseConfig.testConnection()) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Please check your DatabaseConfig.java settings.");
            return;
        }
        System.out.println("✓ Database connected successfully!\n");

        // Create default admin
        AdminDAO adminDAO = new AdminDAO();

        System.out.println("Creating default admin account...");
        System.out.println("Username: admin");
        System.out.println("Password: admin123");
        System.out.println();

        int adminId = adminDAO.createAdmin("admin", "admin123");

        if (adminId > 0) {
            System.out.println("═══════════════════════════════════════════════");
            System.out.println("✓ SUCCESS! Admin account created successfully!");
            System.out.println("═══════════════════════════════════════════════");
            System.out.println();
            System.out.println("Admin Credentials:");
            System.out.println("  Username: admin");
            System.out.println("  Password: admin123");
            System.out.println();
            System.out.println("⚠️  IMPORTANT: Change the default password after first login!");
            System.out.println();
            System.out.println("You can now run the application:");
            System.out.println("  java com.musicrecommendation.ui.LoginScreen");
            System.out.println();
        } else {
            System.err.println("❌ Failed to create admin account!");
            System.err.println("This might be because:");
            System.err.println("1. Admin 'admin' already exists");
            System.err.println("2. Database connection issue");
            System.err.println("3. Admins table doesn't exist");
            System.err.println();
            System.err.println("If admin already exists, you can login with existing credentials.");
        }

        DatabaseConfig.closeConnection();
    }
}