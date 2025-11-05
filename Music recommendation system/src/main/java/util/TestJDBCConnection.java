package main.java.util;

import java.sql.Connection;
import java.sql.SQLException;

public class TestJDBCConnection {

    public static void main(String[] args) {
        System.out.println("JDBC Connection Test");

        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("JDBC connection successful!");
            } else {
                System.out.println("JDBC connection failed!");
            }
        } catch (SQLException e) {
            System.err.println("Error while testing JDBC connection:");
            e.printStackTrace();
        }

        System.out.println("JDBC Test Completed");
    }
}
