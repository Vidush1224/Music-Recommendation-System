package main.java.model;

public class Mood {
    private int moodId;
    private String moodName;
    private String moodDescription;

    public Mood() {
    }

    public Mood(String moodName, String moodDescription) {
        this.moodName = moodName;
        this.moodDescription = moodDescription;
    }

    // Getters and Setters
    public int getMoodId() {
        return moodId;
    }

    public void setMoodId(int moodId) {
        this.moodId = moodId;
    }

    public String getMoodName() {
        return moodName;
    }

    public void setMoodName(String moodName) {
        this.moodName = moodName;
    }

    public String getMoodDescription() {
        return moodDescription;
    }

    public void setMoodDescription(String moodDescription) {
        this.moodDescription = moodDescription;
    }

    @Override
    public String toString() {
        return "Mood{" +
                "moodId=" + moodId +
                ", moodName='" + moodName + '\'' +
                ", moodDescription='" + moodDescription + '\'' +
                '}';
    }
}
