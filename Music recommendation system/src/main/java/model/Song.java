package main.java.model;

import java.sql.Timestamp;

public class Song {
    private int songId;
    private String title;
    private String artist;
    private String genre;
    private int duration;
    private String filePath;
    private Timestamp dateAdded;

    public Song() {
    }

    public Song(String title, String artist, String genre, int duration, String filePath) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.duration = duration;
        this.filePath = filePath;
    }

    // Getters and Setters
    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId=" + songId +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration + " sec" +
                '}';
    }
}
