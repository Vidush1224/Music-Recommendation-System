package main.java.model;

import java.sql.Timestamp;

public class ActivityLog {
    private int logId;
    private String actionType;
    private String tableName;
    private String description;
    private Integer userId;
    private Integer adminId;
    private Timestamp timestamp;

    public ActivityLog() {}

    public ActivityLog(String actionType, String tableName, String description) {
        this.actionType = actionType;
        this.tableName = tableName;
        this.description = description;
    }

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }
    
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}