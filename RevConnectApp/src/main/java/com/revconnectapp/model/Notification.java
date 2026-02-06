package com.revconnectapp.model;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String message;
    private String type;  // Add this field
    private boolean isRead;
    private LocalDateTime createdAt;
    
    // Constructors
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.type = "GENERAL"; // Default type
    }
    
    public Notification(int id, int userId, String message, boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.type = "GENERAL";
        this.createdAt = LocalDateTime.now();
    }
    
    public Notification(int id, int userId, String message, String type, boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    // ADD THESE GETTERS AND SETTERS FOR TYPE
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Helper methods
    public String getFormattedTime() {
        if (createdAt == null) return "Just now";
        
        return String.format("%02d:%02d %02d/%02d", 
            createdAt.getHour(), 
            createdAt.getMinute(),
            createdAt.getMonthValue(),
            createdAt.getDayOfMonth());
    }
    
    // Get emoji for notification type
    public String getTypeEmoji() {
        if (type == null) return "📨";
        
        switch (type.toUpperCase()) {
            case "CONNECTION_REQUEST": return "🤝";
            case "CONNECTION_ACCEPTED": return "✅";
            case "CONNECTION_REJECTED": return "❌";
            case "LIKE": return "❤️";
            case "COMMENT": return "💬";
            case "SHARE": return "🔄";
            case "MENTION": return "👥";
            case "FOLLOWER": return "👤";
            case "SYSTEM": return "⚙️";
            case "SECURITY": return "🔐";
            case "WELCOME": return "🎉";
            default: return "📨";
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s [%s] %s - %s", 
            getTypeEmoji(), 
            getFormattedTime(),
            message,
            isRead ? "Read" : "Unread");
    }
}