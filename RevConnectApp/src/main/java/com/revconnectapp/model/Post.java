package com.revconnectapp.model;

public class Post {
    private int id;
    private int userId;
    private String content;
    private String hashtags;
    private boolean isPromotional;
    private boolean pinned;
    private String createdAt;
    private String username;

    public Post() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getHashtags() { return hashtags; }
    public void setHashtags(String hashtags) { this.hashtags = hashtags; }
    public boolean isPromotional() { return isPromotional; }
    public void setPromotional(boolean promotional) { isPromotional = promotional; }
    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String toString() {
        return content.length() > 50 ? content.substring(0, 47) + "..." : content;
    }
}
