package main.java.dao;

import main.java.util.DatabaseConfig;
import main.java.model.*;

import java.sql.*;
import java.util.*;

public class MoodDAO {

    /**
     * Get all moods from database
     * 
     * @return List of all moods
     */
    public List<Mood> getAllMoods() {
        List<Mood> moods = new ArrayList<>();
        String query = "SELECT * FROM moods ORDER BY mood_name";

        try (Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Mood mood = new Mood();
                mood.setMoodId(rs.getInt("mood_id"));
                mood.setMoodName(rs.getString("mood_name"));
                mood.setMoodDescription(rs.getString("mood_description"));
                moods.add(mood);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching moods: " + e.getMessage());
            e.printStackTrace();
        }

        return moods;
    }

    /**
     * Get mood by ID
     * 
     * @param moodId Mood ID
     * @return Mood object or null
     */
    public Mood getMoodById(int moodId) {
        String query = "SELECT * FROM moods WHERE mood_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, moodId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Mood mood = new Mood();
                mood.setMoodId(rs.getInt("mood_id"));
                mood.setMoodName(rs.getString("mood_name"));
                mood.setMoodDescription(rs.getString("mood_description"));
                return mood;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching mood: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get mood by name
     * 
     * @param moodName Mood name
     * @return Mood object or null
     */
    public Mood getMoodByName(String moodName) {
        String query = "SELECT * FROM moods WHERE mood_name = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, moodName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Mood mood = new Mood();
                mood.setMoodId(rs.getInt("mood_id"));
                mood.setMoodName(rs.getString("mood_name"));
                mood.setMoodDescription(rs.getString("mood_description"));
                return mood;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching mood by name: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Add new mood
     * 
     * @param mood Mood object
     * @return mood_id of inserted mood, -1 if failed
     */
    public int addMood(Mood mood) {
        String query = "INSERT INTO moods (mood_name, mood_description) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, mood.getMoodName());
            pstmt.setString(2, mood.getMoodDescription());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding mood: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }
}
