package main.java.dao;

import main.java.util.DatabaseConfig;
import main.java.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityDAO {

    // Get all activities
    public List<Activity> getAllActivities() {
        List<Activity> activities = new ArrayList<>();
        String query = "SELECT * FROM activities ORDER BY activity_name";

        try (Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Activity activity = new Activity();
                activity.setActivityId(rs.getInt("activity_id"));
                activity.setActivityName(rs.getString("activity_name"));
                activity.setActivityDescription(rs.getString("activity_description"));
                activities.add(activity);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching activities: " + e.getMessage());
            e.printStackTrace();
        }

        return activities;
    }

    // Get activity by ID
    public Activity getActivityById(int activityId) {
        String query = "SELECT * FROM activities WHERE activity_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, activityId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Activity activity = new Activity();
                activity.setActivityId(rs.getInt("activity_id"));
                activity.setActivityName(rs.getString("activity_name"));
                activity.setActivityDescription(rs.getString("activity_description"));
                return activity;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching activity: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Get activity by name
    public Activity getActivityByName(String activityName) {
        String query = "SELECT * FROM activities WHERE activity_name = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, activityName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Activity activity = new Activity();
                activity.setActivityId(rs.getInt("activity_id"));
                activity.setActivityName(rs.getString("activity_name"));
                activity.setActivityDescription(rs.getString("activity_description"));
                return activity;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching activity by name: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Add new activity
    public int addActivity(Activity activity) {
        String query = "INSERT INTO activities (activity_name, activity_description) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, activity.getActivityName());
            pstmt.setString(2, activity.getActivityDescription());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding activity: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }
}
