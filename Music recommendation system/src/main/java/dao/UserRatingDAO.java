package main.java.dao;

import main.java.util.DatabaseConfig;
import main.java.model.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRatingDAO {

    /**
     * Add or update a user's rating for a song
     * 
     * @param userId User ID
     * @param songId Song ID
     * @param rating Rating value (1-5)
     * @return true if successful
     */
    public boolean addOrUpdateRating(int userId, int songId, int rating) {
        if (rating < 1 || rating > 5) {
            System.err.println("Rating must be between 1 and 5");
            return false;
        }

        String query = "INSERT INTO user_ratings (user_id, song_id, rating) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE rating = ?, rated_at = CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, songId);
            pstmt.setInt(3, rating);
            pstmt.setInt(4, rating); // for UPDATE part

            pstmt.executeUpdate();
            System.out.println("✓ Rating saved successfully!");
            return true;

        } catch (SQLException e) {
            System.err.println("Error saving rating: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get user's rating for a specific song
     * 
     * @param userId User ID
     * @param songId Song ID
     * @return Rating value (1-5) or -1 if not rated
     */
    public int getUserRating(int userId, int songId) {
        String query = "SELECT rating FROM user_ratings WHERE user_id = ? AND song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, songId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("rating");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching rating: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Get average rating for a song
     * 
     * @param songId Song ID
     * @return Average rating or 0.0 if no ratings
     */
    public double getAverageSongRating(int songId) {
        String query = "SELECT AVG(rating) as avg_rating FROM user_ratings WHERE song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }

        } catch (SQLException e) {
            System.err.println("Error calculating average rating: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Get top rated songs
     * 
     * @param limit Number of songs to return
     * @return List of top rated songs
     */
    public List<Song> getTopRatedSongs(int limit) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT s.*, AVG(ur.rating) as avg_rating, COUNT(ur.rating_id) as rating_count " +
                "FROM songs s " +
                "INNER JOIN user_ratings ur ON s.song_id = ur.song_id " +
                "GROUP BY s.song_id " +
                "HAVING rating_count >= 1 " +
                "ORDER BY avg_rating DESC, rating_count DESC " +
                "LIMIT ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Song song = new Song();
                song.setSongId(rs.getInt("song_id"));
                song.setTitle(rs.getString("title"));
                song.setArtist(rs.getString("artist"));
                song.setGenre(rs.getString("genre"));
                song.setDuration(rs.getInt("duration"));
                song.setFilePath(rs.getString("file_path"));
                songs.add(song);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching top rated songs: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Get all ratings by a user
     * 
     * @param userId User ID
     * @return List of songs rated by user
     */
    public List<Song> getUserRatedSongs(int userId) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT s.*, ur.rating FROM songs s " +
                "INNER JOIN user_ratings ur ON s.song_id = ur.song_id " +
                "WHERE ur.user_id = ? " +
                "ORDER BY ur.rated_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Song song = new Song();
                song.setSongId(rs.getInt("song_id"));
                song.setTitle(rs.getString("title"));
                song.setArtist(rs.getString("artist"));
                song.setGenre(rs.getString("genre"));
                song.setDuration(rs.getInt("duration"));
                song.setFilePath(rs.getString("file_path"));
                songs.add(song);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user rated songs: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Delete a user's rating
     * 
     * @param userId User ID
     * @param songId Song ID
     * @return true if successful
     */
    public boolean deleteRating(int userId, int songId) {
        String query = "DELETE FROM user_ratings WHERE user_id = ? AND song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, songId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting rating: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get rating count for a song
     * 
     * @param songId Song ID
     * @return Number of ratings
     */
    public int getRatingCount(int songId) {
        String query = "SELECT COUNT(*) as count FROM user_ratings WHERE song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("Error getting rating count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get total ratings by a user
     * 
     * @param userId User ID
     * @return Total number of ratings
     */
    public int getUserRatingCount(int userId) {
        String query = "SELECT COUNT(*) as count FROM user_ratings WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("Error getting user rating count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
}