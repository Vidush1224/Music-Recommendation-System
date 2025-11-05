-- Create Database
CREATE DATABASE IF NOT EXISTS music_project;
USE music_project;

-- Drop tables if they exist (clean rerun)
DROP TABLE IF EXISTS user_favorites;
DROP TABLE IF EXISTS user_ratings;
DROP TABLE IF EXISTS song_activity;
DROP TABLE IF EXISTS song_mood;
DROP TABLE IF EXISTS reverse_mood_mapping;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS activities;
DROP TABLE IF EXISTS moods;
DROP TABLE IF EXISTS songs;

-- Table 1: Songs
CREATE TABLE songs (
    song_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    artist VARCHAR(150) NOT NULL,
    genre VARCHAR(100),
    duration INT,
    file_path VARCHAR(300),
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_song (title, artist)
);

-- Table 2: Moods
CREATE TABLE moods (
    mood_id INT PRIMARY KEY AUTO_INCREMENT,
    mood_name VARCHAR(50) UNIQUE NOT NULL,
    mood_description VARCHAR(255)
);

-- Table 3: Activities
CREATE TABLE activities (
    activity_id INT PRIMARY KEY AUTO_INCREMENT,
    activity_name VARCHAR(50) UNIQUE NOT NULL,
    activity_description VARCHAR(255)
);

-- Table 4: Song_Mood Mapping
CREATE TABLE song_mood (
    song_id INT,
    mood_id INT,
    PRIMARY KEY (song_id, mood_id),
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE,
    FOREIGN KEY (mood_id) REFERENCES moods(mood_id) ON DELETE CASCADE
);

-- Table 5: Song_Activity Mapping
CREATE TABLE song_activity (
    song_id INT,
    activity_id INT,
    PRIMARY KEY (song_id, activity_id),
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id) ON DELETE CASCADE
);

-- Table 6: Users
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 7: User Ratings
CREATE TABLE user_ratings (
    rating_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    song_id INT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE,
    UNIQUE KEY unique_rating (user_id, song_id)
);

-- Table 8: User Favorites
CREATE TABLE user_favorites (
    user_id INT,
    song_id INT,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, song_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE
);

-- Table 9: Reverse Mood Mapping
CREATE TABLE reverse_mood_mapping (
    current_mood_id INT,
    target_mood_id INT,
    PRIMARY KEY (current_mood_id, target_mood_id),
    FOREIGN KEY (current_mood_id) REFERENCES moods(mood_id) ON DELETE CASCADE,
    FOREIGN KEY (target_mood_id) REFERENCES moods(mood_id) ON DELETE CASCADE
);

-- Table 10: Admins
CREATE TABLE admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    admin_username VARCHAR(50) UNIQUE NOT NULL,
    admin_password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert Sample Moods

INSERT INTO moods (mood_name, mood_description) VALUES
('Happy', 'Joyful and upbeat mood'),
('Sad', 'Melancholic and down mood'),
('Energetic', 'High energy and pumped up'),
('Calm', 'Peaceful and relaxed'),
('Angry', 'Frustrated or intense emotions'),
('Romantic', 'Loving and affectionate'),
('Motivated', 'Driven and focused');

-- Insert Sample Activities

INSERT INTO activities (activity_name, activity_description) VALUES
('Studying', 'Focus and concentration required'),
('Workout', 'Physical exercise and training'),
('Relaxing', 'Unwinding and chilling'),
('Partying', 'Social gathering and celebration'),
('Sleeping', 'Resting and bedtime'),
('Commuting', 'Traveling or on the go'),
('Working', 'Professional work environment');

-- Insert Reverse Mood Mapping

INSERT INTO reverse_mood_mapping (current_mood_id, target_mood_id) VALUES
(2, 1), (2, 3), (5, 4), (5, 1), (4, 3), (1, 4);

-- Insert Sample Songs

INSERT INTO songs (title, artist, genre, duration, file_path) VALUES
('Don''t Stop Me Now', 'Queen', 'Rock', 215, '/music/queen_dontstop.mp3'),
('Happy', 'Pharrell Williams', 'Pop', 233, '/music/pharrell_happy.mp3'),
('Eye of the Tiger', 'Survivor', 'Rock', 246, '/music/survivor_eye.mp3'),
('Someone Like You', 'Adele', 'Pop', 285, '/music/adele_someone.mp3'),
('Weightless', 'Marconi Union', 'Ambient', 480, '/music/weightless.mp3'),
('Thinking Out Loud', 'Ed Sheeran', 'Pop', 281, '/music/edsheeran_thinking.mp3'),
('Lose Yourself', 'Eminem', 'Hip Hop', 326, '/music/eminem_lose.mp3');

-- Map Songs to Moods

INSERT INTO song_mood (song_id, mood_id) VALUES
(1, 1),(1, 3),(2, 1),(3, 3),(3, 7),(4, 2),(5, 4),(6, 6),(7, 7),(7, 3);

-- Map Songs to Activities

INSERT INTO song_activity (song_id, activity_id) VALUES
(1, 2),(1, 4),(2, 4),(2, 6),(3, 2),(4, 3),(4, 5),(5, 1),(5, 3),(5, 5),(6, 3),(7, 2),(7, 7);

-- ==============================
-- Insert default admin
-- ==============================
INSERT INTO admins (admin_username, admin_password_hash) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');

-- Create Indexes

CREATE INDEX idx_song_title ON songs(title);
CREATE INDEX idx_song_artist ON songs(artist);
CREATE INDEX idx_user_ratings_song ON user_ratings(song_id);
CREATE INDEX idx_user_favorites_user ON user_favorites(user_id);

-- Add Activity Logs Table
CREATE TABLE activity_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    action_type VARCHAR(20) NOT NULL,
    table_name VARCHAR(50),
    description TEXT,
    user_id INT,
    admin_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (admin_id) REFERENCES admins(admin_id) ON DELETE SET NULL,
    INDEX idx_timestamp (timestamp),
    INDEX idx_action_type (action_type)
);

-- Add sample logs
INSERT INTO activity_logs (action_type, table_name, description, admin_id) VALUES
('INSERT', 'songs', 'Added song: Blinding Lights', 1),
('LOGIN', NULL, 'Admin logged in', 1),
('SELECT', 'songs', 'User searched for songs', NULL);