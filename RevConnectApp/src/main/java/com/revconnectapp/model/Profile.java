package com.revconnectapp.model;

public class Profile {
    private int id, userId;
    private String name, bio, picturePath, location, website, privacy;

    public Profile() {}
    
    public Profile(int userId, String name, String bio, String location, String website) {
        this.userId = userId;
        this.name = name;
        this.bio = bio;
        this.location = location;
        this.website = website;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPicturePath() { return picturePath; }
    public void setPicturePath(String picturePath) { this.picturePath = picturePath; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getPrivacy() { return privacy; }
    public void setPrivacy(String privacy) { this.privacy = privacy; }

    @Override
    public String toString() {
        return String.format("👤 %s\n📍 %s\n🌐 %s\n📝 %s", 
            name != null ? name : "No name",
            location != null ? location : "No location",
            website != null ? website : "No website",
            bio != null ? safePreview(bio) : "No bio");
    }
    
    private String safePreview(String text) {
        if (text == null || text.length() <= 100) return text;
        return text.substring(0, 100) + "...";
    }
}
