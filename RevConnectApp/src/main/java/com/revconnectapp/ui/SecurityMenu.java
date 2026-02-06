package com.revconnectapp.ui;

import com.revconnectapp.model.User;
import com.revconnectapp.service.UserService;
import com.revconnectapp.util.InputUtil;

public class SecurityMenu {
    private User currentUser;
    private UserService userService;
    private String securityQuestion;
    private String securityAnswer;
    private int loginAttempts;
    private String profilePrivacy; // Store privacy setting
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    
    public SecurityMenu(User currentUser, UserService userService, String securityQuestion, 
                       String securityAnswer, int loginAttempts, String profilePrivacy) {
        this.currentUser = currentUser;
        this.userService = userService;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.loginAttempts = loginAttempts;
        this.profilePrivacy = profilePrivacy;
    }
    
    public void show() {
        while (true) {
            System.out.println("\n🔐 === SECURITY SETTINGS ===");
            System.out.println("=".repeat(40));
            System.out.println("1. 🔑 Change Password");
            System.out.println("2. ❓ Set/Update Security Question");
            System.out.println("3. 🔄 Forgot Password (Reset)");
            System.out.println("4. 👁️ View Privacy Settings");
            System.out.println("5. ⚙️ Update Privacy Settings");
            System.out.println("6. 📊 View Security Status");
            System.out.println("0. ↩️ Back to Dashboard");
            
            System.out.print("\nYour choice: ");
            int choice = InputUtil.getInt();
            
            switch (choice) {
                case 1 -> changePassword();
                case 2 -> setSecurityQuestion();
                case 3 -> forgotPassword();
                case 4 -> viewPrivacySettings(); // FIXED: No parameter
                case 5 -> updatePrivacySettings(); // FIXED: No parameter, no return
                case 6 -> viewSecurityStatus(); // FIXED: No parameter
                case 0 -> { return; }
                default -> System.out.println("❌ Invalid choice!");
            }
        }
    }
    
    // ========== 1. CHANGE PASSWORD ==========
    private void changePassword() {
        System.out.println("\n🔑 === CHANGE PASSWORD ===");
        System.out.println("⚠️ For security, verify your current password.");
        
        System.out.print("Current Password: ");
        String currentPassword = InputUtil.getString();
        
        // Verify current password
        if (!currentUser.getPassword().equals(currentPassword)) {
            System.out.println("❌ Incorrect current password!");
            System.out.println("⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        System.out.print("New Password: ");
        String newPassword = InputUtil.getString();
        
        // Check password strength
        String strength = getPasswordStrength(newPassword);
        System.out.println("New Password Strength: " + strength);
        
        if (strength.contains("Weak")) {
            System.out.print("⚠️ Weak password. Continue anyway? (y/n): ");
            if (!InputUtil.getString().equalsIgnoreCase("y")) {
                System.out.println("❌ Password change cancelled.");
                System.out.println("⏎ Press Enter...");
                InputUtil.getString();
                return;
            }
        }
        
        System.out.print("Confirm New Password: ");
        String confirmPassword = InputUtil.getString();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("❌ Passwords don't match!");
        } else if (newPassword.equals(currentPassword)) {
            System.out.println("❌ New password cannot be same as old password!");
        } else {
            // Update password
            boolean updated = userService.updatePassword(currentUser.getId(), newPassword);
            if (updated) {
                currentUser.setPassword(newPassword);
                System.out.println("\n✅ Password changed successfully!");
                System.out.println("🔒 You will be logged out for security.");
                
                System.out.println("\n⏎ Press Enter to continue...");
                InputUtil.getString();
                
                // Signal that user should be logged out
                throw new SecurityException("Password changed - logout required");
            } else {
                System.out.println("❌ Failed to update password!");
            }
        }
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
    
    // ========== 2. SET SECURITY QUESTION ==========
    private void setSecurityQuestion() {
        System.out.println("\n❓ === SECURITY QUESTION ===");
        System.out.println("Used for password recovery if you forget your password.");
        
        if (securityQuestion != null && !securityQuestion.isEmpty()) {
            System.out.println("Current question: " + securityQuestion);
            System.out.print("Update existing question? (y/n): ");
            if (!InputUtil.getString().equalsIgnoreCase("y")) {
                return;
            }
        }
        
        System.out.println("\n📋 Select a security question:");
        System.out.println("1. What was your first pet's name?");
        System.out.println("2. What elementary school did you attend?");
        System.out.println("3. What is your mother's maiden name?");
        System.out.println("4. What city were you born in?");
        System.out.println("5. What is your favorite book?");
        System.out.println("6. Custom question");
        
        System.out.print("\nYour choice (1-6): ");
        int questionChoice = InputUtil.getInt();
        
        String question = "";
        switch (questionChoice) {
            case 1 -> question = "What was your first pet's name?";
            case 2 -> question = "What elementary school did you attend?";
            case 3 -> question = "What is your mother's maiden name?";
            case 4 -> question = "What city were you born in?";
            case 5 -> question = "What is your favorite book?";
            case 6 -> {
                System.out.print("Enter your custom question: ");
                question = InputUtil.getString();
                if (question.trim().isEmpty()) {
                    System.out.println("❌ Question cannot be empty!");
                    return;
                }
            }
            default -> {
                System.out.println("❌ Invalid choice!");
                return;
            }
        }
        
        System.out.print("Answer (case insensitive): ");
        String answer = InputUtil.getString();
        
        if (answer.trim().isEmpty()) {
            System.out.println("❌ Answer cannot be empty!");
        } else {
            // Update in currentUser object if possible
            try {
                currentUser.setSecurityQuestion(question);
                currentUser.setSecurityAnswer(answer.toLowerCase());
            } catch (Exception e) {
                // If User model doesn't have these fields, that's okay
                System.out.println("Note: Security questions will be lost on logout (not stored in database)");
            }
            
            // Update in memory variables
            securityQuestion = question;
            securityAnswer = answer.toLowerCase();
            
            System.out.println("\n✅ Security question set successfully!");
            System.out.println("💡 Question: " + question);
            System.out.println("🔐 Answer saved securely.");
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
    
    // ========== 3. FORGOT PASSWORD ==========
    private void forgotPassword() {
        System.out.println("\n🔄 === FORGOT PASSWORD ===");
        System.out.println("Reset your password using security questions.");
        
        System.out.print("Username: ");
        String username = InputUtil.getString();
        
        System.out.print("Email: ");
        String email = InputUtil.getString();
        
        // Verify user exists
        User user = userService.getUserByUsername(username);
        if (user == null || !user.getEmail().equals(email)) {
            System.out.println("❌ Invalid username or email!");
            System.out.println("⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        // Check if security question is set
        if (securityQuestion == null || securityQuestion.isEmpty()) {
            System.out.println("❌ Security question not set for this account!");
            System.out.println("⚠️ Please contact support for assistance.");
            System.out.println("⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        System.out.println("\n🔐 Security Question: " + securityQuestion);
        System.out.print("Your answer: ");
        String answer = InputUtil.getString().toLowerCase();
        
        if (!answer.equals(securityAnswer)) {
            System.out.println("❌ Incorrect answer!");
            System.out.println("⚠️ Please try again or contact support.");
            System.out.println("⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        // Reset password
        System.out.println("\n✅ Identity verified!");
        System.out.println("Now set your new password.");
        
        System.out.print("New Password: ");
        String newPassword = InputUtil.getString();
        
        // Check password strength
        String strength = getPasswordStrength(newPassword);
        System.out.println("Password Strength: " + strength);
        
        if (strength.contains("Weak")) {
            System.out.print("⚠️ Weak password. Continue anyway? (y/n): ");
            if (!InputUtil.getString().equalsIgnoreCase("y")) {
                System.out.println("❌ Password reset cancelled.");
                System.out.println("⏎ Press Enter...");
                InputUtil.getString();
                return;
            }
        }
        
        System.out.print("Confirm New Password: ");
        String confirmPassword = InputUtil.getString();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("❌ Passwords don't match!");
        } else if (newPassword.length() < 6) {
            System.out.println("❌ Password must be at least 6 characters!");
        } else {
            boolean updated = userService.updatePassword(user.getId(), newPassword);
            if (updated) {
                System.out.println("\n✅ Password reset successfully!");
                System.out.println("🔓 Account unlocked! Login attempts reset.");
                System.out.println("📧 You can now login with your new password.");
                
                // Reset login attempts
                loginAttempts = 0;
                
                // If current user is logged in, signal logout
                if (currentUser != null && currentUser.getId() == user.getId()) {
                    System.out.println("🔒 Logging out for security...");
                    throw new SecurityException("Password reset - logout required");
                }
            } else {
                System.out.println("❌ Failed to reset password!");
            }
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
    
    // ========== 4. VIEW PRIVACY SETTINGS ==========
    private void viewPrivacySettings() {
        System.out.println("\n👁️ === PRIVACY SETTINGS ===");
        System.out.println("=".repeat(40));
        System.out.println("🔒 Current Privacy Level: " + profilePrivacy);
        System.out.println("-".repeat(40));
        
        System.out.println("\n📋 What each setting means:");
        System.out.println("\nPUBLIC:");
        System.out.println("  ✓ Profile visible to everyone");
        System.out.println("  ✓ Anyone can see your posts");
        System.out.println("  ✓ Anyone can send connection requests");
        
        System.out.println("\nPRIVATE:");
        System.out.println("  ✓ Only connections see your profile");
        System.out.println("  ✓ Only connections see your posts");
        System.out.println("  ✓ Connection requests from followers only");
        
        System.out.println("\n👤 Your current visibility:");
        System.out.println("Profile: " + (profilePrivacy.equals("PUBLIC") ? "Everyone" : "Connections Only"));
        System.out.println("Posts: " + (profilePrivacy.equals("PUBLIC") ? "Everyone" : "Connections Only"));
        System.out.println("Connection Requests: " + (profilePrivacy.equals("PUBLIC") ? "Anyone" : "Followers Only"));
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
    
    // ========== 5. UPDATE PRIVACY SETTINGS ==========
    private void updatePrivacySettings() {
        System.out.println("\n⚙️ === UPDATE PRIVACY ===");
        System.out.println("Current setting: " + profilePrivacy);
        
        System.out.println("\nSelect new privacy level:");
        System.out.println("1. 🔓 PUBLIC - Maximum visibility");
        System.out.println("2. 🔒 PRIVATE - Enhanced privacy");
        System.out.println("3. ⚠️ Cancel (no changes)");
        
        System.out.print("\nYour choice (1-3): ");
        int choice = InputUtil.getInt();
        
        // REMOVED: String oldPrivacy = profilePrivacy; // Unused variable
        
        switch (choice) {
            case 1 -> {
                profilePrivacy = "PUBLIC";
                System.out.println("\n✅ Privacy set to PUBLIC");
                System.out.println("🌐 Your profile is now visible to everyone");
            }
            case 2 -> {
                profilePrivacy = "PRIVATE";
                System.out.println("\n✅ Privacy set to PRIVATE");
                System.out.println("🔒 Your profile is now only visible to connections");
            }
            default -> {
                System.out.println("⚠️ Privacy settings unchanged");
                return;
            }
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
    
    // ========== 6. VIEW SECURITY STATUS ==========
    private void viewSecurityStatus() {
        System.out.println("\n🛡️ === SECURITY STATUS ===");
        System.out.println("=".repeat(40));
        
        System.out.println("👤 Account: @" + currentUser.getUsername());
        System.out.println("📧 Email: " + currentUser.getEmail());
        System.out.println("🔐 Account Type: " + currentUser.getUserType());
        
        System.out.println("\n📊 SECURITY FEATURES STATUS:");
        System.out.println("Password Strength: " + getPasswordStrength(currentUser.getPassword()));
        System.out.println("Security Question: " + 
            ((securityQuestion == null || securityQuestion.isEmpty()) ? "❌ Not Set" : "✅ Configured"));
        System.out.println("Privacy Level: " + profilePrivacy);
        System.out.println("Login Attempts: " + loginAttempts + " (Recent)");
        System.out.println("Account Lock Status: " + 
            (loginAttempts >= MAX_LOGIN_ATTEMPTS ? "🔒 Locked" : "🔓 Active"));
        
        System.out.println("\n💡 SECURITY TIPS:");
        System.out.println("1. Use strong, unique passwords");
        System.out.println("2. Set up security questions");
        System.out.println("3. Use Private mode for enhanced privacy");
        System.out.println("4. Never share your password");
        System.out.println("5. Log out from shared devices");
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
    
    // ========== PASSWORD STRENGTH CHECKER ==========
    private String getPasswordStrength(String password) {
        if (password == null || password.length() < 6) return "❌ Very Weak";
        if (password.length() < 8) return "⚠️ Weak";
        
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        int score = 0;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;
        
        if (password.length() >= 12 && score == 4) return "💪 Excellent";
        if (score >= 3) return "✅ Strong";
        if (score >= 2) return "⚠️ Fair";
        return "❌ Weak";
    }
    
    // ========== GETTERS FOR UPDATED VALUES ==========
    public String getSecurityQuestion() {
        return securityQuestion;
    }
    
    public String getSecurityAnswer() {
        return securityAnswer;
    }
    
    public int getLoginAttempts() {
        return loginAttempts;
    }
    
    public String getProfilePrivacy() {
        return profilePrivacy;
    }
}

// Custom exception for security-related actions requiring logout
//Custom exception for security-related actions requiring logout
class SecurityException extends RuntimeException {
 private static final long serialVersionUID = 1L;
 
 public SecurityException(String message) {
     super(message);
 }
}
