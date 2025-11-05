package main.java.service;

import main.java.dao.*;
import main.java.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendationService {

    private SongDAO songDAO;
    private MoodDAO moodDAO;
    private ActivityDAO activityDAO;
    private UserRatingDAO userRatingDAO;
    private UserFavoriteDAO userFavoriteDAO;

    public RecommendationService() {
        this.songDAO = new SongDAO();
        this.moodDAO = new MoodDAO();
        this.activityDAO = new ActivityDAO();
        this.userRatingDAO = new UserRatingDAO();
        this.userFavoriteDAO = new UserFavoriteDAO();
    }

    /**
     * Get personalized recommendations based on mood and activity
     * 
     * @param moodName     Mood name (e.g., "Happy", "Sad")
     * @param activityName Activity name (e.g., "Studying", "Workout")
     * @param userId       User ID for personalization (optional, use 0 if not
     *                     logged in)
     * @return List of recommended songs
     */
    public List<Song> getRecommendations(String moodName, String activityName, int userId) {
        Mood mood = moodDAO.getMoodByName(moodName);
        Activity activity = activityDAO.getActivityByName(activityName);

        if (mood == null || activity == null) {
            System.err.println("Invalid mood or activity!");
            return new ArrayList<>();
        }

        // Get songs matching BOTH mood AND activity (Your USP #1)
        List<Song> recommendations = songDAO.getSongsByMoodAndActivity(
                mood.getMoodId(),
                activity.getActivityId());

        // If user is logged in, personalize based on their history
        if (userId > 0 && !recommendations.isEmpty()) {
            recommendations = personalizeRecommendations(recommendations, userId);
        }

        return recommendations;
    }

    /**
     * Get mood-shifting recommendations (USP #2 - Reverse Mood Intelligence!)
     * 
     * @param currentMoodName Current mood
     * @param activityName    Current activity
     * @param userId          User ID
     * @return List of mood-shifting songs
     */
    public List<Song> getMoodShiftingRecommendations(String currentMoodName,
            String activityName,
            int userId) {
        Mood currentMood = moodDAO.getMoodByName(currentMoodName);
        Activity activity = activityDAO.getActivityByName(activityName);

        if (currentMood == null || activity == null) {
            System.err.println("Invalid mood or activity!");
            return new ArrayList<>();
        }

        // Get songs for OPPOSITE mood (Your USP #2)
        List<Song> moodShiftingSongs = songDAO.getSongsForReverseMood(
                currentMood.getMoodId(),
                activity.getActivityId());

        // Fallback: If no reverse mood songs, get energetic/happy songs
        if (moodShiftingSongs.isEmpty()) {
            System.out.println("No direct mood-shifting songs found. Getting uplifting alternatives...");
            Mood happyMood = moodDAO.getMoodByName("Happy");
            if (happyMood != null) {
                moodShiftingSongs = songDAO.getSongsByMoodAndActivity(
                        happyMood.getMoodId(),
                        activity.getActivityId());
            }

            // If still empty, try Energetic
            if (moodShiftingSongs.isEmpty()) {
                Mood energeticMood = moodDAO.getMoodByName("Energetic");
                if (energeticMood != null) {
                    moodShiftingSongs = songDAO.getSongsByMoodAndActivity(
                            energeticMood.getMoodId(),
                            activity.getActivityId());
                }
            }
        }

        if (userId > 0 && !moodShiftingSongs.isEmpty()) {
            moodShiftingSongs = personalizeRecommendations(moodShiftingSongs, userId);
        }

        return moodShiftingSongs;
    }

    /**
     * Personalize recommendations based on user's history
     * 
     * @param songs  List of candidate songs
     * @param userId User ID
     * @return Personalized list of songs
     */
    private List<Song> personalizeRecommendations(List<Song> songs, int userId) {
        // Create a scoring map
        Map<Integer, Double> songScores = new HashMap<>();

        for (Song song : songs) {
            double score = 0.0;

            // Factor 1: User's own rating (highest weight)
            int userRating = userRatingDAO.getUserRating(userId, song.getSongId());
            if (userRating > 0) {
                score += userRating * 2.0; // Double weight for user's own ratings
            }

            // Factor 2: Average rating from all users
            double avgRating = userRatingDAO.getAverageSongRating(song.getSongId());
            score += avgRating;

            // Factor 3: Favorite status (bonus points)
            if (userFavoriteDAO.isFavorite(userId, song.getSongId())) {
                score += 5.0; // Bonus for favorited songs
            }

            // Factor 4: Popularity (favorite count)
            int favoriteCount = userFavoriteDAO.getFavoriteCount(song.getSongId());
            score += Math.log(favoriteCount + 1) * 0.5; // Logarithmic scaling

            songScores.put(song.getSongId(), score);
        }

        // Sort songs by score (descending)
        songs.sort((s1, s2) -> {
            double score1 = songScores.getOrDefault(s1.getSongId(), 0.0);
            double score2 = songScores.getOrDefault(s2.getSongId(), 0.0);
            return Double.compare(score2, score1);
        });

        return songs;
    }

    /**
     * Get smart recommendations when user doesn't specify full criteria
     * 
     * @param userId User ID
     * @return Mixed recommendations
     */
    public List<Song> getSmartRecommendations(int userId) {
        List<Song> recommendations = new ArrayList<>();

        // Strategy 1: Get user's favorite songs
        if (userId > 0) {
            List<Song> favorites = userFavoriteDAO.getUserFavorites(userId);
            recommendations.addAll(favorites);
        }

        // Strategy 2: Get top rated songs
        List<Song> topRated = userRatingDAO.getTopRatedSongs(5);
        for (Song song : topRated) {
            if (!containsSong(recommendations, song)) {
                recommendations.add(song);
            }
        }

        // Strategy 3: Get most favorited songs
        List<Song> mostFavorited = userFavoriteDAO.getMostFavoritedSongs(5);
        for (Song song : mostFavorited) {
            if (!containsSong(recommendations, song)) {
                recommendations.add(song);
            }
        }

        return recommendations;
    }

    /**
     * Get recommendations by genre with mood/activity filtering
     * 
     * @param genre        Genre name
     * @param moodName     Mood (optional)
     * @param activityName Activity (optional)
     * @return List of songs
     */
    public List<Song> getRecommendationsByGenre(String genre, String moodName, String activityName) {
        List<Song> allSongs = songDAO.getSongsByGenre(genre);

        // If mood/activity specified, further filter
        if (moodName != null && activityName != null) {
            Mood mood = moodDAO.getMoodByName(moodName);
            Activity activity = activityDAO.getActivityByName(activityName);

            if (mood != null && activity != null) {
                List<Song> moodActivitySongs = songDAO.getSongsByMoodAndActivity(
                        mood.getMoodId(),
                        activity.getActivityId());

                // Keep only songs that are in both lists
                allSongs.retainAll(moodActivitySongs);
            }
        }

        return allSongs;
    }

    /**
     * Get contextual recommendations based on time of day
     * 
     * @param userId User ID
     * @return Time-appropriate recommendations
     */
    public List<Song> getContextualRecommendations(int userId) {
        java.time.LocalTime now = java.time.LocalTime.now();
        int hour = now.getHour();

        String suggestedMood;
        String suggestedActivity;

        if (hour >= 6 && hour < 9) {
            // Morning: Energetic music
            suggestedMood = "Energetic";
            suggestedActivity = "Commuting";
        } else if (hour >= 9 && hour < 12) {
            // Late morning: Focus music
            suggestedMood = "Motivated";
            suggestedActivity = "Working";
        } else if (hour >= 12 && hour < 14) {
            // Lunch time: Relaxing music
            suggestedMood = "Calm";
            suggestedActivity = "Relaxing";
        } else if (hour >= 14 && hour < 18) {
            // Afternoon: Productive music
            suggestedMood = "Motivated";
            suggestedActivity = "Working";
        } else if (hour >= 18 && hour < 21) {
            // Evening: Upbeat or relaxing
            suggestedMood = "Happy";
            suggestedActivity = "Relaxing";
        } else if (hour >= 21 && hour < 23) {
            // Night: Calm music
            suggestedMood = "Calm";
            suggestedActivity = "Relaxing";
        } else {
            // Late night: Sleep music
            suggestedMood = "Calm";
            suggestedActivity = "Sleeping";
        }

        System.out.println("🕐 Contextual recommendation for " + hour + ":00");
        System.out.println("   Suggested: " + suggestedMood + " + " + suggestedActivity);

        return getRecommendations(suggestedMood, suggestedActivity, userId);
    }

    /**
     * Get recommendations based on user's favorites pattern
     * 
     * @param userId User ID
     * @return Similar songs to user's favorites
     */
    public List<Song> getRecommendationsBasedOnFavorites(int userId) {
        List<Song> recommendations = new ArrayList<>();
        List<Song> favorites = userFavoriteDAO.getUserFavorites(userId);

        if (favorites.isEmpty()) {
            return recommendations;
        }

        // Find most common moods and activities in user's favorites
        Map<Integer, Integer> moodFrequency = new HashMap<>();
        Map<Integer, Integer> activityFrequency = new HashMap<>();

        for (Song favSong : favorites) {
            List<Integer> moods = songDAO.getMoodsBySong(favSong.getSongId());
            List<Integer> activities = songDAO.getActivitiesBysong(favSong.getSongId());

            for (int moodId : moods) {
                moodFrequency.put(moodId, moodFrequency.getOrDefault(moodId, 0) + 1);
            }

            for (int activityId : activities) {
                activityFrequency.put(activityId, activityFrequency.getOrDefault(activityId, 0) + 1);
            }
        }

        // Get most frequent mood and activity
        int topMoodId = getMaxKey(moodFrequency);
        int topActivityId = getMaxKey(activityFrequency);

        if (topMoodId > 0 && topActivityId > 0) {
            recommendations = songDAO.getSongsByMoodAndActivity(topMoodId, topActivityId);
            // Remove songs already in favorites
            recommendations.removeAll(favorites);
        }

        return recommendations;
    }

    /**
     * Helper method to get key with maximum value from map
     */
    private int getMaxKey(Map<Integer, Integer> map) {
        int maxKey = 0;
        int maxValue = 0;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                maxKey = entry.getKey();
            }
        }

        return maxKey;
    }

    /**
     * Helper method to check if song list contains a song
     */
    private boolean containsSong(List<Song> songs, Song targetSong) {
        for (Song song : songs) {
            if (song.getSongId() == targetSong.getSongId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all available moods
     */
    public List<Mood> getAvailableMoods() {
        return moodDAO.getAllMoods();
    }

    /**
     * Get all available activities
     */
    public List<Activity> getAvailableActivities() {
        return activityDAO.getAllActivities();
    }
}