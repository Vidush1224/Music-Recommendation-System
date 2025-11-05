package main.java.dao;

import main.java.util.DatabaseConfig;
import main.java.model.Song;
import main.java.dao.ActivityLogDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDAO {

    /**
     * Add a new song to the database
     * 
     * @param song Song object to add
     * @return song_id of inserted song, -1 if failed
     */
    public int addSong(Song song) {
        String query = "INSERT INTO songs (title, artist, genre, duration, file_path) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, song.getTitle());
            pstmt.setString(2, song.getArtist());
            pstmt.setString(3, song.getGenre());
            pstmt.setInt(4, song.getDuration());
            pstmt.setString(5, song.getFilePath());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int songId = generatedKeys.getInt(1);
                    System.out.println("Song added successfully with ID: " + songId);

                    // ✨ ADD ACTIVITY LOGGING HERE
                    try {
                        ActivityLogDAO logDAO = new ActivityLogDAO();
                        logDAO.logActivity(
                                "INSERT",
                                "songs",
                                "Added song: " + song.getTitle() + " by " + song.getArtist(),
                                null, // user_id (null for admin action)
                                1 // admin_id (you can make this dynamic later)
                        );
                    } catch (Exception e) {
                        System.err.println("Failed to log activity: " + e.getMessage());
                    }

                    return songId;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding song: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Get song by ID
     * 
     * @param songId ID of the song
     * @return Song object or null if not found
     */
    public Song getSongById(int songId) {
        String query = "SELECT * FROM songs WHERE song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractSongFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching song: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get all songs from database
     * 
     * @return List of all songs
     */
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT * FROM songs ORDER BY title";

        try (Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                songs.add(extractSongFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all songs: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Get songs by mood and activity (YOUR USP #1!)
     * 
     * @param moodId     Mood ID
     * @param activityId Activity ID
     * @return List of matching songs
     */
    public List<Song> getSongsByMoodAndActivity(int moodId, int activityId) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT DISTINCT s.* FROM songs s " +
                "INNER JOIN song_mood sm ON s.song_id = sm.song_id " +
                "INNER JOIN song_activity sa ON s.song_id = sa.song_id " +
                "WHERE sm.mood_id = ? AND sa.activity_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, moodId);
            pstmt.setInt(2, activityId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                songs.add(extractSongFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching songs by mood and activity: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Get songs for reverse mood (YOUR USP #2 - Mood Shifting!)
     * 
     * @param currentMoodId Current mood ID
     * @param activityId    Activity ID
     * @return List of songs for target mood
     */
    public List<Song> getSongsForReverseMood(int currentMoodId, int activityId) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT DISTINCT s.* FROM songs s " +
                "INNER JOIN song_mood sm ON s.song_id = sm.song_id " +
                "INNER JOIN song_activity sa ON s.song_id = sa.song_id " +
                "INNER JOIN reverse_mood_mapping rmm ON sm.mood_id = rmm.target_mood_id " +
                "WHERE rmm.current_mood_id = ? AND sa.activity_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, currentMoodId);
            pstmt.setInt(2, activityId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                songs.add(extractSongFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching reverse mood songs: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Get songs by mood only
     * 
     * @param moodId Mood ID
     * @return List of matching songs
     */
    public List<Song> getSongsByMood(int moodId) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT DISTINCT s.* FROM songs s " +
                "INNER JOIN song_mood sm ON s.song_id = sm.song_id " +
                "WHERE sm.mood_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, moodId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                songs.add(extractSongFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching songs by mood: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Get songs by activity only
     * 
     * @param activityId Activity ID
     * @return List of matching songs
     */
    public List<Song> getSongsByActivity(int activityId) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT DISTINCT s.* FROM songs s " +
                "INNER JOIN song_activity sa ON s.song_id = sa.song_id " +
                "WHERE sa.activity_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, activityId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                songs.add(extractSongFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching songs by activity: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Update song details
     * 
     * @param song Song object with updated details
     * @return true if successful
     */
    public boolean updateSong(Song song) {
        String query = "UPDATE songs SET title = ?, artist = ?, genre = ?, duration = ?, file_path = ? WHERE song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, song.getTitle());
            pstmt.setString(2, song.getArtist());
            pstmt.setString(3, song.getGenre());
            pstmt.setInt(4, song.getDuration());
            pstmt.setString(5, song.getFilePath());
            pstmt.setInt(6, song.getSongId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Song updated successfully!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error updating song: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete song by ID
     * 
     * @param songId Song ID to delete
     * @return true if successful
     */
    public boolean deleteSong(int songId) {
        String query = "DELETE FROM songs WHERE song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Song deleted successfully!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error deleting song: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Add mood tag to a song
     * 
     * @param songId Song ID
     * @param moodId Mood ID
     * @return true if successful
     */
    public boolean addMoodToSong(int songId, int moodId) {
        String query = "INSERT INTO song_mood (song_id, mood_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            pstmt.setInt(2, moodId);
            pstmt.executeUpdate();
            System.out.println("Mood tag added successfully!");
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("This mood is already tagged to this song!");
            } else {
                System.err.println("Error adding mood to song: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Add activity tag to a song
     * 
     * @param songId     Song ID
     * @param activityId Activity ID
     * @return true if successful
     */
    public boolean addActivityToSong(int songId, int activityId) {
        String query = "INSERT INTO song_activity (song_id, activity_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            pstmt.setInt(2, activityId);
            pstmt.executeUpdate();
            System.out.println("Activity tag added successfully!");
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("This activity is already tagged to this song!");
            } else {
                System.err.println("Error adding activity to song: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Remove mood tag from a song
     * 
     * @param songId Song ID
     * @param moodId Mood ID
     * @return true if successful
     */
    public boolean removeMoodFromSong(int songId, int moodId) {
        String query = "DELETE FROM song_mood WHERE song_id = ? AND mood_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            pstmt.setInt(2, moodId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error removing mood from song: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Remove activity tag from a song
     * 
     * @param songId     Song ID
     * @param activityId Activity ID
     * @return true if successful
     */
    public boolean removeActivityFromSong(int songId, int activityId) {
        String query = "DELETE FROM song_activity WHERE song_id = ? AND activity_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            pstmt.setInt(2, activityId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error removing activity from song: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Search songs by title or artist
     * 
     * @param searchTerm Search keyword
     * @return List of matching songs
     */
    public List<Song> searchSongs(String searchTerm) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT * FROM songs WHERE title LIKE ? OR artist LIKE ? ORDER BY title";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            String search = "%" + searchTerm + "%";
            pstmt.setString(1, search);
            pstmt.setString(2, search);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                songs.add(extractSongFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching songs: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Get songs by genre
     * 
     * @param genre Genre name
     * @return List of songs in that genre
     */
    public List<Song> getSongsByGenre(String genre) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT * FROM songs WHERE genre = ? ORDER BY title";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, genre);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                songs.add(extractSongFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching songs by genre: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Get songs by artist
     * 
     * @param artist Artist name
     * @return List of songs by that artist
     */
    public List<Song> getSongsByArtist(String artist) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT * FROM songs WHERE artist LIKE ? ORDER BY title";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            String search = "%" + artist + "%";
            pstmt.setString(1, search);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                songs.add(extractSongFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching songs by artist: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Get all moods tagged to a song
     * 
     * @param songId Song ID
     * @return List of mood IDs
     */
    public List<Integer> getMoodsBySong(int songId) {
        List<Integer> moodIds = new ArrayList<>();
        String query = "SELECT mood_id FROM song_mood WHERE song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                moodIds.add(rs.getInt("mood_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching moods for song: " + e.getMessage());
            e.printStackTrace();
        }

        return moodIds;
    }

    /**
     * Get all activities tagged to a song
     * 
     * @param songId Song ID
     * @return List of activity IDs
     */
    public List<Integer> getActivitiesBysong(int songId) {
        List<Integer> activityIds = new ArrayList<>();
        String query = "SELECT activity_id FROM song_activity WHERE song_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, songId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                activityIds.add(rs.getInt("activity_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching activities for song: " + e.getMessage());
            e.printStackTrace();
        }

        return activityIds;
    }

    /**
     * Get count of songs by mood
     * 
     * @param moodId Mood ID
     * @return Number of songs
     */
    public int getSongCountByMood(int moodId) {
        String query = "SELECT COUNT(DISTINCT song_id) as count FROM song_mood WHERE mood_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, moodId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("Error counting songs by mood: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get count of songs by activity
     * 
     * @param activityId Activity ID
     * @return Number of songs
     */
    public int getSongCountByActivity(int activityId) {
        String query = "SELECT COUNT(DISTINCT song_id) as count FROM song_activity WHERE activity_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, activityId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("Error counting songs by activity: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Check if a song exists by title and artist
     * 
     * @param title  Song title
     * @param artist Artist name
     * @return true if exists
     */
    public boolean songExists(String title, String artist) {
        String query = "SELECT COUNT(*) FROM songs WHERE title = ? AND artist = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, artist);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking song existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get total number of songs in database
     * 
     * @return Total song count
     */
    public int getTotalSongCount() {
        String query = "SELECT COUNT(*) as count FROM songs";

        try (Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("Error counting total songs: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Helper method to extract Song object from ResultSet
     * 
     * @param rs ResultSet
     * @return Song object
     * @throws SQLException
     */
    private Song extractSongFromResultSet(ResultSet rs) throws SQLException {
        Song song = new Song();
        song.setSongId(rs.getInt("song_id"));
        song.setTitle(rs.getString("title"));
        song.setArtist(rs.getString("artist"));
        song.setGenre(rs.getString("genre"));
        song.setDuration(rs.getInt("duration"));
        song.setFilePath(rs.getString("file_path"));
        song.setDateAdded(rs.getTimestamp("date_added"));
        return song;
    }
}