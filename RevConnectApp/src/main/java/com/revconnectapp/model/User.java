package com.revconnectapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String userType; // PERSONAL, CREATOR, BUSINESS
    private LocalDateTime createdAt;
    
    
    // Security fields
    private String securityQuestion;
    private String securityAnswer;
    
    // Profile fields
    private String fullName;
    private String bio;
    private String location;
    private String website;
    private String privacySetting = "PUBLIC"; // PUBLIC or PRIVATE
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    // Constructor
    public User() {
        this.createdAt = LocalDateTime.now();
    }
    
    public User(String username, String email, String password, String userType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.createdAt = LocalDateTime.now();
    }
    
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { 
        this.username = username != null ? username.trim() : null; 
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email != null ? email.trim() : null; 
    }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { 
        this.password = password != null ? password : null; 
    }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { 
        this.userType = userType != null ? userType.trim().toUpperCase() : null; 
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(String securityQuestion) { 
        this.securityQuestion = securityQuestion != null ? securityQuestion.trim() : null; 
    }
    
    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { 
        this.securityAnswer = securityAnswer != null ? securityAnswer.trim().toLowerCase() : null; 
    }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { 
        this.fullName = fullName != null ? fullName.trim() : null; 
    }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { 
        this.bio = bio != null ? bio.trim() : null; 
    }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { 
        this.location = location != null ? location.trim() : null; 
    }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { 
        this.website = website != null ? website.trim() : null; 
    }
    
    public String getPrivacySetting() { return privacySetting; }
    public void setPrivacySetting(String privacySetting) {
        if (privacySetting != null && 
            (privacySetting.trim().equalsIgnoreCase("PUBLIC") || 
             privacySetting.trim().equalsIgnoreCase("PRIVATE"))) {
            this.privacySetting = privacySetting.trim().toUpperCase();
        }
    }
    
    // ========== SECURITY METHODS ==========
    
    /**
     * Set security question and answer
     */
    public void setSecurityInfo(String question, String answer) {
        this.securityQuestion = question != null ? question.trim() : null;
        this.securityAnswer = answer != null ? answer.trim().toLowerCase() : null;
    }
    
    /**
     * Check if user has security question set
     */
    public boolean hasSecurityQuestion() {
        return securityQuestion != null && !securityQuestion.isEmpty() &&
               securityAnswer != null && !securityAnswer.isEmpty();
    }
    
    /**
     * Verify security answer (case-insensitive)
     */
    public boolean verifySecurityAnswer(String answer) {
        if (securityAnswer == null || answer == null) return false;
        return securityAnswer.equals(answer.trim().toLowerCase());
    }
    
    /**
     * Check if user can reset password
     */
    public boolean canResetPassword() {
        return hasSecurityQuestion();
    }
    
    /**
     * Get security info for display (hides answer)
     */
    public String getSecurityInfo() {
        return hasSecurityQuestion() ? 
            "Question: " + securityQuestion + "\nAnswer: ******" : 
            "No security question set";
    }
    
    // ========== PASSWORD METHODS ==========
    
    /**
     * Check if password meets minimum requirements
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Get password strength indicator
     */
    public static String getPasswordStrength(String password) {
        if (password == null) return "❌ Invalid";
        if (password.length() < 6) return "❌ Very Weak";
        if (password.length() < 8) return "⚠️ Weak";
        
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isWhitespace(c)) hasSpecial = true;
        }
        
        int score = (hasUpper ? 1 : 0) + (hasLower ? 1 : 0) + 
                   (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);
        
        if (password.length() >= 12 && score == 4) return "💪 Excellent";
        if (score >= 3) return "✅ Strong";
        if (score >= 2) return "⚠️ Fair";
        return "❌ Weak";
    }
    
    // ========== EMAIL METHODS ==========
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    // ========== USER TYPE METHODS ==========
    
    /**
     * Get user type display name
     */
    public String getUserTypeDisplay() {
        if (userType == null) return "Unknown";
        return switch (userType.toUpperCase()) {
            case "PERSONAL" -> "Personal Account";
            case "CREATOR" -> "Creator Account";
            case "BUSINESS" -> "Business Account";
            default -> userType;
        };
    }
    
    /**
     * Check if user can connect with another user
     */
    public boolean canConnectWith(User otherUser) {
        if (otherUser == null || this.userType == null || otherUser.userType == null) {
            return false;
        }
        return this.userType.equals("PERSONAL") && otherUser.userType.equals("PERSONAL");
    }
    
    // ========== PRIVACY METHODS ==========
    
    /**
     * Get privacy setting display
     */
    public String getPrivacyDisplay() {
        return switch (privacySetting.toUpperCase()) {
            case "PUBLIC" -> "Public (Everyone can see)";
            case "PRIVATE" -> "Private (Connections only)";
            default -> privacySetting;
        };
    }
    
    public boolean isPublicProfile() {
        return "PUBLIC".equals(privacySetting);
    }
    
    public boolean isPrivateProfile() {
        return "PRIVATE".equals(privacySetting);
    }
    
    // ========== VALIDATION METHODS ==========
    
    /**
     * Check if user data is valid
     */
    public boolean isValid() {
        return validate().isEmpty();
    }
    
    /**
     * Validate user data and return list of errors
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        
        // Username validation
        if (username == null || username.trim().isEmpty()) {
            errors.add("Username is required");
        } else if (username.trim().length() < 3) {
            errors.add("Username must be at least 3 characters");
        } else if (!username.matches("^[a-zA-Z0-9._-]{3,}$")) {
            errors.add("Username can only contain letters, numbers, dots, hyphens, and underscores");
        }
        
        // Email validation
        if (email == null || email.trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!isValidEmail(email)) {
            errors.add("Email format is invalid");
        }
        
        // Password validation
        if (password == null || password.isEmpty()) {
            errors.add("Password is required");
        } else if (!isValidPassword(password)) {
            errors.add("Password must be at least 6 characters");
        }
        
        // User type validation
        if (userType == null || userType.trim().isEmpty()) {
            errors.add("User type is required");
        } else if (!userType.matches("(?i)PERSONAL|CREATOR|BUSINESS")) {
            errors.add("User type must be PERSONAL, CREATOR, or BUSINESS");
        }
        
        return errors;
    }
    
    // ========== DISPLAY METHODS ==========
    
    /**
     * Get user preview for display
     */
    public String getPreview() {
        String securityStatus = hasSecurityQuestion() ? "🔐 Secured" : "⚠️ Basic Security";
        return String.format("@%s (%s) - %s", 
            username != null ? username : "N/A", 
            getUserTypeDisplay(),
            securityStatus);
    }
    
    /**
     * Get security status summary
     */
    public String getSecurityStatus() {
        StringBuilder status = new StringBuilder();
        
        if (hasSecurityQuestion()) {
            status.append("✅ Security Question Set\n");
        } else {
            status.append("❌ No Security Question\n");
        }
        
        status.append("🔑 Password Strength: ").append(getPasswordStrength(password)).append("\n");
        status.append("🔒 Privacy: ").append(getPrivacyDisplay());
        
        return status.toString();
    }
    
    /**
     * Get complete user info for display
     */
    public String getCompleteInfo() {
        StringBuilder info = new StringBuilder();
        info.append("👤 USER PROFILE\n");
        info.append("=".repeat(40)).append("\n");
        info.append("Username: ").append(username != null ? "@" + username : "N/A").append("\n");
        info.append("Email: ").append(email != null ? email : "N/A").append("\n");
        info.append("Account Type: ").append(getUserTypeDisplay()).append("\n");
        info.append("User ID: ").append(id).append("\n");
        info.append("Created: ").append(createdAt != null ? createdAt : "N/A").append("\n");
        
        if (fullName != null && !fullName.isEmpty()) {
            info.append("Full Name: ").append(fullName).append("\n");
        }
        
        if (bio != null && !bio.isEmpty()) {
            info.append("Bio: ").append(bio.length() > 100 ? bio.substring(0, 100) + "..." : bio).append("\n");
        }
        
        if (location != null && !location.isEmpty()) {
            info.append("Location: ").append(location).append("\n");
        }
        
        if (website != null && !website.isEmpty()) {
            info.append("Website: ").append(website).append("\n");
        }
        
        info.append("\n🔐 SECURITY STATUS\n");
        info.append("-".repeat(40)).append("\n");
        info.append(getSecurityStatus());
        
        return info.toString();
    }
    
    // ========== EQUALS & HASHCODE ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', email='%s', type='%s', security=%s}", 
            id, username, email, userType, hasSecurityQuestion() ? "YES" : "NO");
    }
}