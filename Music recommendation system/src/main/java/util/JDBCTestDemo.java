package main.java.util;

import main.java.util.DatabaseConfig;
import main.java.dao.*;
import main.java.model.*;

import java.util.List;
import java.util.Scanner;

/**
 * Test class to demonstrate JDBC connectivity and operations
 * This class tests all database operations
 */
public class JDBCTestDemo {

    private static SongDAO songDAO = new SongDAO();
    private static MoodDAO moodDAO = new MoodDAO();
    private static ActivityDAO activityDAO = new ActivityDAO();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Music Recommendation System - JDBC Test ===\n");

        // Test 1: Database Connection
        testDatabaseConnection();

        // Main menu
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Test Song Operations (CRUD)");
            System.out.println("2. Test Mood & Activity Operations");
            System.out.println("3. Test Recommendation Queries");
            System.out.println("4. Test Reverse Mood Feature");
            System.out.println("5. Add New Song with Tags");
            System.out.println("6. Search Songs");
            System.out.println("7. View All Data");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    testSongOperations();
                    break;
                case 2:
                    testMoodActivityOperations();
                    break;
                case 3:
                    testRecommendationQueries();
                    break;
                case 4:
                    testReverseMoodFeature();
                    break;
                case 5:
                    addNewSongWithTags();
                    break;
                case 6:
                    searchSongs();
                    break;
                case 7:
                    viewAllData();
                    break;
                case 0:
                    exit = true;
                    DatabaseConfig.closeConnection();
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    /**
     * Test 1: Database Connection
     */
    private static void testDatabaseConnection() {
        System.out.println("--- Test 1: Database Connection ---");
        if (DatabaseConfig.testConnection()) {
            System.out.println("✓ Database connected successfully!");
            System.out.println("✓ Database URL: " + DatabaseConfig.getDatabaseURL());
        } else {
            System.out.println("✗ Database connection failed!");
            System.out.println("Please check your database credentials in DatabaseConfig.java");
        }
    }

    /**
     * Test 2: Song CRUD Operations
     */
    private static void testSongOperations() {
        System.out.println("\n--- Test 2: Song CRUD Operations ---");

        // CREATE - Add a new song
        System.out.println("\n1. Adding a new song...");
        Song newSong = new Song("Believer", "Imagine Dragons", "Alternative Rock", 204, "/music/believer.mp3");
        int songId = songDAO.addSong(newSong);

        if (songId > 0) {
            System.out.println("✓ Song added with ID: " + songId);

            // READ - Fetch the song
            System.out.println("\n2. Reading the song...");
            Song fetchedSong = songDAO.getSongById(songId);
            if (fetchedSong != null) {
                System.out.println("✓ Song fetched: " + fetchedSong);
            }

            // UPDATE - Update song details
            System.out.println("\n3. Updating song details...");
            fetchedSong.setGenre("Pop Rock");
            if (songDAO.updateSong(fetchedSong)) {
                System.out.println("✓ Song updated successfully!");
                System.out.println("✓ Updated song: " + songDAO.getSongById(songId));
            }

            // DELETE - Delete the song
            System.out.print("\n4. Do you want to delete this test song? (y/n): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("y")) {
                if (songDAO.deleteSong(songId)) {
                    System.out.println("✓ Song deleted successfully!");
                }
            }
        }
    }

    /**
     * Test 3: Mood and Activity Operations
     */
    private static void testMoodActivityOperations() {
        System.out.println("\n--- Test 3: Mood & Activity Operations ---");

        // Fetch all moods
        System.out.println("\n1. All Moods:");
        List<Mood> moods = moodDAO.getAllMoods();
        for (Mood mood : moods) {
            System.out.println("   " + mood);
        }

        // Fetch all activities
        System.out.println("\n2. All Activities:");
        List<Activity> activities = activityDAO.getAllActivities();
        for (Activity activity : activities) {
            System.out.println("   " + activity);
        }

        // Get specific mood
        System.out.println("\n3. Fetching 'Happy' mood:");
        Mood happyMood = moodDAO.getMoodByName("Happy");
        if (happyMood != null) {
            System.out.println("   " + happyMood);
        }
    }

    /**
     * Test 4: Recommendation Queries (Mood + Activity)
     */
    private static void testRecommendationQueries() {
        System.out.println("\n--- Test 4: Recommendation Queries ---");

        // Display available moods
        List<Mood> moods = moodDAO.getAllMoods();
        System.out.println("\nAvailable Moods:");
        for (int i = 0; i < moods.size(); i++) {
            System.out.println((i + 1) + ". " + moods.get(i).getMoodName());
        }
        System.out.print("Select mood (enter number): ");
        int moodChoice = scanner.nextInt();
        scanner.nextLine();

        // Display available activities
        List<Activity> activities = activityDAO.getAllActivities();
        System.out.println("\nAvailable Activities:");
        for (int i = 0; i < activities.size(); i++) {
            System.out.println((i + 1) + ". " + activities.get(i).getActivityName());
        }
        System.out.print("Select activity (enter number): ");
        int activityChoice = scanner.nextInt();
        scanner.nextLine();

        if (moodChoice > 0 && moodChoice <= moods.size() &&
                activityChoice > 0 && activityChoice <= activities.size()) {

            Mood selectedMood = moods.get(moodChoice - 1);
            Activity selectedActivity = activities.get(activityChoice - 1);

            System.out.println(
                    "\nSearching for: " + selectedMood.getMoodName() + " + " + selectedActivity.getActivityName());

            List<Song> recommendations = songDAO.getSongsByMoodAndActivity(
                    selectedMood.getMoodId(),
                    selectedActivity.getActivityId());

            if (recommendations.isEmpty()) {
                System.out.println("No songs found for this combination.");
            } else {
                System.out.println("\n✓ Recommended Songs:");
                for (Song song : recommendations) {
                    System.out.println("   • " + song);
                }
            }
        }
    }

    /**
     * Test 5: Reverse Mood Feature
     */
    private static void testReverseMoodFeature() {
        System.out.println("\n--- Test 5: Reverse Mood Feature (Mood Shifting) ---");

        // Example: User is Sad, wants to feel better
        System.out.println("\nScenario: User is feeling 'Sad' and wants mood improvement");

        Mood sadMood = moodDAO.getMoodByName("Sad");
        Activity relaxingActivity = activityDAO.getActivityByName("Relaxing");

        if (sadMood != null && relaxingActivity != null) {
            System.out.println("Current Mood: " + sadMood.getMoodName());
            System.out.println("Activity: " + relaxingActivity.getActivityName());

            List<Song> uplifitingSongs = songDAO.getSongsForReverseMood(
                    sadMood.getMoodId(),
                    relaxingActivity.getActivityId());

            if (uplifitingSongs.isEmpty()) {
                System.out.println("No mood-shifting songs available for this combination.");
            } else {
                System.out.println("\n✓ Mood-Shifting Recommendations (to make you feel better):");
                for (Song song : uplifitingSongs) {
                    System.out.println("   • " + song);
                }
            }
        }
    }

    /**
     * Test 6: Add New Song with Tags
     */
    private static void addNewSongWithTags() {
        System.out.println("\n--- Add New Song with Mood & Activity Tags ---");

        // Get song details
        System.out.print("Enter song title: ");
        String title = scanner.nextLine();

        System.out.print("Enter artist name: ");
        String artist = scanner.nextLine();

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine();

        System.out.print("Enter duration (in seconds): ");
        int duration = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter file path: ");
        String filePath = scanner.nextLine();

        // Create and add song
        Song song = new Song(title, artist, genre, duration, filePath);
        int songId = songDAO.addSong(song);

        if (songId > 0) {
            System.out.println("✓ Song added successfully with ID: " + songId);

            // Add mood tags
            System.out.println("\nSelect mood tags (enter numbers separated by commas):");
            List<Mood> moods = moodDAO.getAllMoods();
            for (int i = 0; i < moods.size(); i++) {
                System.out.println((i + 1) + ". " + moods.get(i).getMoodName());
            }
            System.out.print("Mood tags: ");
            String moodInput = scanner.nextLine();
            String[] moodIndices = moodInput.split(",");

            for (String index : moodIndices) {
                try {
                    int moodIndex = Integer.parseInt(index.trim()) - 1;
                    if (moodIndex >= 0 && moodIndex < moods.size()) {
                        songDAO.addMoodToSong(songId, moods.get(moodIndex).getMoodId());
                        System.out.println("✓ Added mood: " + moods.get(moodIndex).getMoodName());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: " + index);
                }
            }

            // Add activity tags
            System.out.println("\nSelect activity tags (enter numbers separated by commas):");
            List<Activity> activities = activityDAO.getAllActivities();
            for (int i = 0; i < activities.size(); i++) {
                System.out.println((i + 1) + ". " + activities.get(i).getActivityName());
            }
            System.out.print("Activity tags: ");
            String activityInput = scanner.nextLine();
            String[] activityIndices = activityInput.split(",");

            for (String index : activityIndices) {
                try {
                    int activityIndex = Integer.parseInt(index.trim()) - 1;
                    if (activityIndex >= 0 && activityIndex < activities.size()) {
                        songDAO.addActivityToSong(songId, activities.get(activityIndex).getActivityId());
                        System.out.println("✓ Added activity: " + activities.get(activityIndex).getActivityName());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: " + index);
                }
            }

            System.out.println("\n✓ Song added with all tags successfully!");
        }
    }

    /**
     * Test 7: Search Songs
     */
    private static void searchSongs() {
        System.out.println("\n--- Search Songs ---");
        System.out.print("Enter search term (title or artist): ");
        String searchTerm = scanner.nextLine();

        List<Song> results = songDAO.searchSongs(searchTerm);

        if (results.isEmpty()) {
            System.out.println("No songs found matching: " + searchTerm);
        } else {
            System.out.println("\n✓ Search Results:");
            for (Song song : results) {
                System.out.println("   • " + song);
            }
        }
    }

    /**
     * View All Data
     */
    private static void viewAllData() {
        System.out.println("\n--- View All Database Data ---");

        System.out.println("\n1. All Songs:");
        List<Song> songs = songDAO.getAllSongs();
        if (songs.isEmpty()) {
            System.out.println("   No songs in database.");
        } else {
            for (Song song : songs) {
                System.out.println("   • " + song);
            }
        }

        System.out.println("\n2. All Moods:");
        List<Mood> moods = moodDAO.getAllMoods();
        for (Mood mood : moods) {
            System.out.println("   • " + mood);
        }

        System.out.println("\n3. All Activities:");
        List<Activity> activities = activityDAO.getAllActivities();
        for (Activity activity : activities) {
            System.out.println("   • " + activity);
        }
    }
}