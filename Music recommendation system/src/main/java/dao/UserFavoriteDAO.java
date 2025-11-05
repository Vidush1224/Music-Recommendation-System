package main.java.dao;

import main.java.util.DatabaseConfig;
import main.java.model.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserFavoriteDAO {

    /**
     * Add song to user's favorites
     * 
     * @param userId User ID
     * @param songId Song ID
     * @return true if successful
     */
    public boolean addToFavorites(int userId, int songId) {
        String query = "INSERT INTO user_favorites (user_id, song_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, songId);
            pstmt.executeUpdate();
            System.out.println("✓ Song added to favorites!");
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.out.println("Song already in favorites!");
            } else {
                System.err.println("Error adding to favorites: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Remove song from user's favorites
     * 
     * @param userId User ID
     * @param songId Song ID
     * @return true if successful
     */
    public boolean removeFromFavorites(int userId, int songId) {
        String query = "DELETE FROM user_favorites WHERE user_id = ? AND song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, songId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Song removed from favorites!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error removing from favorites: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get all favorite songs for a user
     * 
     * @param userId User ID
     * @return List of favorite songs
     */
    public List<Song> getUserFavorites(int userId) {
        List<Song> favorites = new ArrayList<>();
        String query = "SELECT s.* FROM songs s " +
                "INNER JOIN user_favorites uf ON s.song_id = uf.song_id " +
                "WHERE uf.user_id = ? " +
                "ORDER BY uf.saved_at DESC";

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
                favorites.add(song);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching favorites: " + e.getMessage());
            e.printStackTrace();
        }

        return favorites;
    }

    /**
     * Check if a song is in user's favorites
     * 
     * @param userId User ID
     * @param songId Song ID
     * @return true if song is favorited
     */
    public boolean isFavorite(int userId, int songId) {
        String query = "SELECT COUNT(*) FROM user_favorites WHERE user_id = ? AND song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, songId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking favorite status: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get count of users who favorited a song
     * 
     * @param songId Song ID
     * @return Number of users who favorited this song
     */
    public int getFavoriteCount(int songId) {
        String query = "SELECT COUNT(*) FROM user_favorites WHERE song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting favorite count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get user's favorite count
     * 
     * @param userId User ID
     * @return Number of favorites
     */
    public int getUserFavoriteCount(int userId) {
        String query = "SELECT COUNT(*) FROM user_favorites WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting user favorite count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get most favorited songs
     * 
     * @param limit Number of songs to return
     * @return List of most favorited songs
     */
    public List<Song> getMostFavoritedSongs(int limit) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT s.*, COUNT(uf.user_id) as favorite_count " +
                "FROM songs s " +
                "INNER JOIN user_favorites uf ON s.song_id = uf.song_id " +
                "GROUP BY s.song_id " +
                "ORDER BY favorite_count DESC " +
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
            System.err.println("Error fetching most favorited songs: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Get favorites by mood and activity
     * 
     * @param userId     User ID
     * @param moodId     Mood ID
     * @param activityId Activity ID
     * @return List of favorite songs matching criteria
     */
    public List<Song> getFavoritesByMoodAndActivity(int userId, int moodId, int activityId) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT DISTINCT s.* FROM songs s " +
                "INNER JOIN user_favorites uf ON s.song_id = uf.song_id " +
                "INNER JOIN song_mood sm ON s.song_id = sm.song_id " +
                "INNER JOIN song_activity sa ON s.song_id = sa.song_id " +
                "WHERE uf.user_id = ? AND sm.mood_id = ? AND sa.activity_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, moodId);
            pstmt.setInt(3, activityId);
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
            System.err.println("Error fetching favorites by mood and activity: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Clear all favorites for a user
     * 
     * @param userId User ID
     * @return true if successful
     */
    public boolean clearAllFavorites(int userId) {
        String query = "DELETE FROM user_favorites WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            System.out.println("✓ All favorites cleared!");
            return true;

        } catch (SQLException e) {
            System.err.println("Error clearing favorites: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}