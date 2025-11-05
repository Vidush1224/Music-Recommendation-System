package main.java.dao;

import main.java.util.DatabaseConfig;
import main.java.model.ActivityLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Activity Log Operations
 * Tracks all database actions for admin monitoring
 */
public class ActivityLogDAO {

    /**
     * Add new activity log entry
     */
    public boolean logActivity(String action, String tableName, String description, Integer userId, Integer adminId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            String query = "INSERT INTO activity_logs (action_type, table_name, description, user_id, admin_id, timestamp) "
                    +
                    "VALUES (?, ?, ?, ?, ?, NOW())";

            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, action);
            pstmt.setString(2, tableName);
            pstmt.setString(3, description);

            if (userId != null) {
                pstmt.setInt(4, userId);
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            if (adminId != null) {
                pstmt.setInt(5, adminId);
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error logging activity: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get all activity logs (most recent first)
     */
    public List<ActivityLog> getAllLogs(int limit) {
        List<ActivityLog> logs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            String query = "SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setLogId(rs.getInt("log_id"));
                log.setActionType(rs.getString("action_type"));
                log.setTableName(rs.getString("table_name"));
                log.setDescription(rs.getString("description"));
                log.setUserId(rs.getInt("user_id"));
                log.setAdminId(rs.getInt("admin_id"));
                log.setTimestamp(rs.getTimestamp("timestamp"));
                logs.add(log);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching logs: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return logs;
    }

    /**
     * Get logs by action type
     */
    public List<ActivityLog> getLogsByAction(String actionType, int limit) {
        List<ActivityLog> logs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            String query = "SELECT * FROM activity_logs WHERE action_type = ? ORDER BY timestamp DESC LIMIT ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, actionType);
            pstmt.setInt(2, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setLogId(rs.getInt("log_id"));
                log.setActionType(rs.getString("action_type"));
                log.setTableName(rs.getString("table_name"));
                log.setDescription(rs.getString("description"));
                log.setUserId(rs.getInt("user_id"));
                log.setAdminId(rs.getInt("admin_id"));
                log.setTimestamp(rs.getTimestamp("timestamp"));
                logs.add(log);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching logs by action: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return logs;
    }

    /**
     * Get logs by date range
     */
    public List<ActivityLog> getLogsByDateRange(Date startDate, Date endDate) {
        List<ActivityLog> logs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            String query = "SELECT * FROM activity_logs WHERE DATE(timestamp) BETWEEN ? AND ? ORDER BY timestamp DESC";
            pstmt = conn.prepareStatement(query);
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setLogId(rs.getInt("log_id"));
                log.setActionType(rs.getString("action_type"));
                log.setTableName(rs.getString("table_name"));
                log.setDescription(rs.getString("description"));
                log.setUserId(rs.getInt("user_id"));
                log.setAdminId(rs.getInt("admin_id"));
                log.setTimestamp(rs.getTimestamp("timestamp"));
                logs.add(log);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching logs by date: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return logs;
    }

    /**
     * Clear old logs (older than X days)
     */
    public boolean clearOldLogs(int daysOld) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            String query = "DELETE FROM activity_logs WHERE timestamp < DATE_SUB(NOW(), INTERVAL ? DAY)";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, daysOld);

            int deleted = pstmt.executeUpdate();
            System.out.println("Deleted " + deleted + " old log entries.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error clearing old logs: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get activity statistics
     */
    public java.util.Map<String, Integer> getActivityStats() {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.createStatement();

            String query = "SELECT action_type, COUNT(*) as count FROM activity_logs GROUP BY action_type";
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                stats.put(rs.getString("action_type"), rs.getInt("count"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching activity stats: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return stats;
    }
}