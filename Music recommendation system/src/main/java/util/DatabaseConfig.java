package main.java.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/music_project";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Vidush@101";

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static Connection connection = null;

    private DatabaseConfig() {
    }

    /**
     * Establishes and returns a database connection
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);

            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connection established successfully!");
            }

            return connection;

        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found!");
            e.printStackTrace();
            throw new SQLException("Failed to load JDBC driver", e);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database!");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Creates a new connection
     * 
     * @return new Connection object
     * @throws SQLException if connection fails
     */
    public static Connection createNewConnection() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Failed to load JDBC driver", e);
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection!");
            e.printStackTrace();
        }
    }

    /**
     * Test database connection
     * 
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get database URL (for configuration display)
     * 
     * @return database URL
     */
    public static String getDatabaseURL() {
        return DB_URL;
    }
}