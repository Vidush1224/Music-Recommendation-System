package main.java.model;

import java.sql.Timestamp;

public class Admin {
    private int adminId;
    private String adminUsername;
    private String adminPasswordHash;
    private Timestamp createdAt;

    public Admin() {
    }

    public Admin(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPasswordHash() {
        return adminPasswordHash;
    }

    public void setAdminPasswordHash(String adminPasswordHash) {
        this.adminPasswordHash = adminPasswordHash;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", adminUsername='" + adminUsername + '\'' +
                '}';
    }
}
