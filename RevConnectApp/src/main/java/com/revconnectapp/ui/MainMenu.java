package com.revconnectapp.ui;

import com.revconnectapp.model.User;
import com.revconnectapp.model.Post;
import com.revconnectapp.model.Comment;
import com.revconnectapp.model.Connection;
import com.revconnectapp.service.UserService;
import com.revconnectapp.service.PostService;
import com.revconnectapp.service.ConnectionService;
import com.revconnectapp.service.NotificationService;
import com.revconnectapp.service.LikeService;
import com.revconnectapp.service.CommentService;
import com.revconnectapp.util.InputUtil;
import java.util.List;
import java.util.ArrayList;

public class MainMenu {
    private UserService userService = new UserService();
    private PostService postService = new PostService();
    private NotificationService notificationService = new NotificationService();
    private ConnectionService connectionService = new ConnectionService();
    private LikeService likeService = new LikeService();
    private CommentService commentService = new CommentService();
    private User currentUser;

    // Profile storage
    private String profileName = "", profileBio = "", profileLocation = "", profileWebsite = "", profilePrivacy = "PUBLIC";
    
    // Security storage (TEMPORARY - will use User model fields after DB update)
    private String securityQuestion = "";
    private String securityAnswer = "";
    private int loginAttempts = 0;
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public void start() {
        System.out.println("=== REVCONNECT APP ===");
        System.out.println("🚀 Complete Social Network with All Features!");
        String choice = InputUtil.getChoice("Login", "Register", "Exit");
        
        if (choice.equals("Login")) login();
        else if (choice.equals("Register")) register();
        else System.exit(0);
        
        if (currentUser != null) showDashboard();
    }

    private void login() {
        System.out.println("\n🔐 === LOGIN ===");
        
        // Check if account is locked
        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            System.out.println("❌ Account temporarily locked due to too many failed attempts!");
            System.out.println("🔓 Please use 'Forgot Password' to reset your password.");
            
            System.out.println("\nOptions:");
            System.out.println("1. Forgot Password (Reset password)");
            System.out.println("2. Reset login attempts (if you remember password)");
            System.out.println("3. Back to Main Menu");
            System.out.print("\nYour choice: ");
            int choice = InputUtil.getInt();
            
            switch (choice) {
                case 1 -> forgotPasswordFromLogin();
                case 2 -> {
                    loginAttempts = 0;
                    System.out.println("✅ Login attempts reset. Try logging in again.");
                    login();
                }
                case 3 -> start();
                default -> login();
            }
            return;
        }
        
        System.out.println("1. Login with Username/Password");
        System.out.println("2. Forgot Password");
        System.out.println("3. Back to Main Menu");
        System.out.print("\nYour choice: ");
        int choice = InputUtil.getInt();
        
        switch (choice) {
            case 1 -> loginWithCredentials();
            case 2 -> forgotPasswordFromLogin();
            case 3 -> start();
            default -> login();
        }
    }

    private void loginWithCredentials() {
        System.out.print("\nUsername: "); 
        String username = InputUtil.getString();
        System.out.print("Password: "); 
        String password = InputUtil.getString();
        
        currentUser = userService.login(username, password);
        if (currentUser == null) {
            loginAttempts++;
            System.out.println("\n❌ Invalid credentials! Attempt " + loginAttempts + "/" + MAX_LOGIN_ATTEMPTS);
            
            if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                System.out.println("⚠️ Account locked! Too many failed attempts.");
                System.out.println("🔓 Please use 'Forgot Password' to reset your password.");
                
                System.out.println("\n⏎ Press Enter to continue...");
                InputUtil.getString();
                
                // Go back to login, not dashboard
                currentUser = null; // Ensure currentUser is null
                login();
            } else {
                System.out.println("\nOptions:");
                System.out.println("1. Try again");
                System.out.println("2. Forgot Password");
                System.out.println("3. Back to Main Menu");
                System.out.print("\nYour choice: ");
                int retryChoice = InputUtil.getInt();
                
                switch (retryChoice) {
                    case 1 -> loginWithCredentials();
                    case 2 -> forgotPasswordFromLogin();
                    case 3 -> {
                        currentUser = null;
                        start();
                    }
                    default -> {
                        currentUser = null;
                        login();
                    }
                }
            }
        } else {
            loginAttempts = 0; // Reset attempts on successful login
            System.out.println("\n✅ Welcome back, " + currentUser.getUsername() + "!");
            System.out.println("📊 Account Type: " + currentUser.getUserType());
            
            // Load security data from User object
            if (currentUser.getSecurityQuestion() != null && !currentUser.getSecurityQuestion().isEmpty()) {
                securityQuestion = currentUser.getSecurityQuestion();
                securityAnswer = currentUser.getSecurityAnswer();
                System.out.println("✅ Security features enabled");
            } else {
                System.out.println("💡 Security Tip: Set up security questions in Security menu!");
            }
            
            // Only show dashboard if login was successful
            showDashboard();
        }
    }

    private void register() {
        System.out.println("\n📝 === REGISTER NEW ACCOUNT ===");
        
        System.out.print("Username: "); 
        String username = InputUtil.getString();
        System.out.print("Email: "); 
        String email = InputUtil.getString();
        
        // Password with validation
        String password = "";
        while (true) {
            System.out.println("\n🔑 PASSWORD REQUIREMENTS:");
            System.out.println("• Minimum 8 characters");
            System.out.println("• Mix of letters and numbers");
            System.out.println("• Strong passwords include special characters");
            System.out.println("-".repeat(40));
            
            System.out.print("Password: "); 
            password = InputUtil.getString();
            
            // Check password strength
            String strength = getPasswordStrength(password);
            System.out.println("Password Strength: " + strength);
            
            if (strength.contains("Weak")) {
                System.out.print("⚠️ Weak password. Continue anyway? (y/n): ");
                if (!InputUtil.getString().equalsIgnoreCase("y")) {
                    continue;
                }
            }
            
            System.out.print("Confirm Password: "); 
            String confirmPassword = InputUtil.getString();
            
            if (!password.equals(confirmPassword)) {
                System.out.println("❌ Passwords don't match! Try again.");
                continue;
            }
            
            break;
        }
        
        String userType = InputUtil.getChoice("PERSONAL", "CREATOR", "BUSINESS");
        
        User newUser = new User(username, email, password, userType);
        currentUser = userService.register(newUser);
        if (currentUser != null) {
            System.out.println("\n✅ Account created successfully!");
            System.out.println("👤 Welcome to RevConnect, " + currentUser.getUsername() + "!");
            
            // Ask to set up security
            System.out.print("\n💡 Set up security question now? (y/n): ");
            if (InputUtil.getString().equalsIgnoreCase("y")) {
                setSecurityQuestion();
            }
            
            System.out.println("\n🔐 You can manage all security settings in the Security menu.");
        } else {
            System.out.println("❌ Username/Email already exists!"); 
            System.out.print("Try again? (y/n): ");
            if (InputUtil.getString().equalsIgnoreCase("y")) {
                register();
            } else {
                start();
            }
        }
    }

    private void showDashboard() {
        int unreadCount = notificationService.getUnreadCount(currentUser.getId());
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🎯 DASHBOARD for @" + currentUser.getUsername() + " 🔔(" + unreadCount + ")");
        System.out.println("=".repeat(50));
        System.out.println("👤 User Type: " + currentUser.getUserType() + " | ID: " + currentUser.getId());
        System.out.println("🔒 Privacy: " + profilePrivacy + 
                         " | 🛡️ Security: " + (securityQuestion.isEmpty() ? "Basic" : "Enhanced"));
        
        // Show connection stats
        showConnectionStats();
        
        // Updated menu with Security option
        String choice = InputUtil.getChoice("👤 Profile", "📱 Feed", "🔔 Notifications", 
                                           "👥 Connections", "📊 Analytics", "🔐 Security", "🚪 Logout");
        
        switch (choice) {
            case "👤 Profile" -> profileMenu();
            case "📱 Feed" -> showFeedMenu();
            case "🔔 Notifications" -> new NotificationMenu().show(currentUser);
            case "👥 Connections" -> showConnectionsMenu();
            case "📊 Analytics" -> showAnalyticsMenu();
            case "🔐 Security" -> securityMenu();
            default -> { 
                currentUser = null; 
                loginAttempts = 0; // Reset login attempts on logout
                securityQuestion = "";
                securityAnswer = "";
                System.out.println("👋 Goodbye!");
                start(); 
            }
        }
        showDashboard();
    }

    // ========== NEW SECURITY MENU ==========
    private void securityMenu() {
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
            case 4 -> viewPrivacySettings();
            case 5 -> updatePrivacySettings();
            case 6 -> viewSecurityStatus();
            default -> {}
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
                
                // Log out
                currentUser = null;
                loginAttempts = 0;
                securityQuestion = "";
                securityAnswer = "";
                start();
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
        
        if (currentUser.getSecurityQuestion() != null && !currentUser.getSecurityQuestion().isEmpty()) {
            System.out.println("Current question: " + currentUser.getSecurityQuestion());
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
            // Store in currentUser object
            currentUser.setSecurityQuestion(question);
            currentUser.setSecurityAnswer(answer.toLowerCase());
            
            // Also update in memory variables for compatibility
            securityQuestion = question;
            securityAnswer = answer.toLowerCase();
            
            System.out.println("\n✅ Security question set successfully!");
            System.out.println("💡 Question: " + question);
            System.out.println("🔐 Answer saved securely.");
            
            // Update in database
            userService.updateSecurityQuestion(currentUser.getId(), question, answer.toLowerCase());
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
        String userSecurityQuestion = user.getSecurityQuestion();
        if (userSecurityQuestion == null || userSecurityQuestion.isEmpty()) {
            System.out.println("❌ Security question not set for this account!");
            System.out.println("⚠️ Please contact support for assistance.");
            System.out.println("⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        System.out.println("\n🔐 Security Question: " + userSecurityQuestion);
        System.out.print("Your answer: ");
        String answer = InputUtil.getString().toLowerCase();
        
        String storedAnswer = user.getSecurityAnswer();
        if (storedAnswer == null || !answer.equals(storedAnswer.toLowerCase())) {
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
                
                // If current user is logged in, log them out
                if (currentUser != null && currentUser.getId() == user.getId()) {
                    System.out.println("🔒 Logging out for security...");
                    currentUser = null;
                    securityQuestion = "";
                    securityAnswer = "";
                    System.out.println("\n⏎ Press Enter to continue...");
                    InputUtil.getString();
                    start();
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
            default -> System.out.println("⚠️ Privacy settings unchanged");
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
            (currentUser.getSecurityQuestion() != null && !currentUser.getSecurityQuestion().isEmpty() ? 
             "✅ Configured" : "❌ Not Set"));
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
    
    private void forgotPasswordFromLogin() {
        System.out.println("\n🔄 === FORGOT PASSWORD ===");
        System.out.println("Reset your password using security questions.");
        System.out.println("Note: You need to have set up security questions previously.");
        
        System.out.print("Username: ");
        String username = InputUtil.getString();
        
        System.out.print("Email: ");
        String email = InputUtil.getString();
        
        // Verify user exists
        User user = userService.getUserByUsername(username);
        if (user == null || !user.getEmail().equals(email)) {
            System.out.println("❌ Invalid username or email!");
            System.out.println("⏎ Press Enter to continue...");
            InputUtil.getString();
            login();
            return;
        }
        
        // Check security question
        String userSecurityQuestion = user.getSecurityQuestion();
        if (userSecurityQuestion == null || userSecurityQuestion.isEmpty()) {
            System.out.println("❌ Security question not set for this account!");
            System.out.println("⚠️ Please contact support for assistance.");
            System.out.println("⏎ Press Enter to continue...");
            InputUtil.getString();
            login();
            return;
        }
        
        // Security question exists, ask it
        System.out.println("\n🔐 Security Question: " + userSecurityQuestion);
        System.out.print("Your answer: ");
        String answer = InputUtil.getString().toLowerCase();
        
        String storedAnswer = user.getSecurityAnswer();
        if (storedAnswer == null || !answer.equals(storedAnswer.toLowerCase())) {
            System.out.println("❌ Incorrect answer!");
            System.out.println("⚠️ Please try again or contact support.");
            System.out.println("⏎ Press Enter to continue...");
            InputUtil.getString();
            login();
            return;
        }
        
        // Reset password
        System.out.println("\n✅ Identity verified!");
        System.out.println("Now set your new password.");
        
        String newPassword;
        while (true) {
            System.out.print("New Password: ");
            newPassword = InputUtil.getString();
            
            // Check password strength
            String strength = getPasswordStrength(newPassword);
            System.out.println("Password Strength: " + strength);
            
            if (strength.contains("Weak")) {
                System.out.print("⚠️ Weak password. Continue anyway? (y/n): ");
                if (!InputUtil.getString().equalsIgnoreCase("y")) {
                    continue;
                }
            }
            
            System.out.print("Confirm New Password: ");
            String confirmPassword = InputUtil.getString();
            
            if (!newPassword.equals(confirmPassword)) {
                System.out.println("❌ Passwords don't match! Try again.");
                continue;
            }
            
            if (newPassword.length() < 6) {
                System.out.println("❌ Password must be at least 6 characters!");
                continue;
            }
            
            break;
        }
        
        boolean updated = userService.updatePassword(user.getId(), newPassword);
        if (updated) {
            System.out.println("\n🎉 PASSWORD RESET SUCCESSFUL!");
            System.out.println("✅ Password changed successfully");
            System.out.println("🔓 Account unlocked! Login attempts reset.");
            System.out.println("📧 You can now login with your new password.");
            
            // Reset login attempts
            loginAttempts = 0;
            
            System.out.println("\n⏎ Press Enter to login with new password...");
            InputUtil.getString();
            
            // Go back to login
            currentUser = null;
            securityQuestion = "";
            securityAnswer = "";
            login();
        } else {
            System.out.println("❌ Failed to reset password!");
            System.out.println("⏎ Press Enter to continue...");
            InputUtil.getString();
            login();
        }
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

    // ========== UPDATE EXISTING PROFILE MENU WITH SECURITY ==========
    private void profileMenu() {
        System.out.println("\n👤 === MY PROFILE ===");
        System.out.println("1. 📝 Edit Profile");
        System.out.println("2. 🔍 Search Users");
        System.out.println("3. 👁️ View My Info");
        System.out.println("4. 🔐 Quick Security");
        System.out.println("0. Back");
        
        int choice = InputUtil.getInt();
        switch (choice) {
            case 1 -> editProfile();
            case 2 -> searchUsers();
            case 3 -> viewMyInfo();
            case 4 -> quickSecurityMenu();
            default -> {}
        }
    }

    private void quickSecurityMenu() {
        System.out.println("\n🔐 === QUICK SECURITY ===");
        System.out.println("1. 🔑 Change Password");
        System.out.println("2. ❓ Update Security Question");
        System.out.println("3. ⚙️ Privacy Settings");
        System.out.println("0. ↩️ Back");
        
        int choice = InputUtil.getInt();
        switch (choice) {
            case 1 -> changePassword();
            case 2 -> setSecurityQuestion();
            case 3 -> updatePrivacySettings();
            default -> {}
        }
    }

    // ========== FEED MENU ==========
    private void showFeedMenu() {
        System.out.println("\n📱 === SOCIAL FEED ===");
        System.out.println("1. ➕ Create New Post");
        System.out.println("2. 📋 View & Manage My Posts");
        System.out.println("3. 🌐 Browse All Public Posts");
        System.out.println("4. 🔍 Search Posts by User");
        System.out.println("5. 🤝 Connections Feed");  // ADDED THIS OPTION
        System.out.println("0. Back to Dashboard");
        
        int choice = InputUtil.getInt();
        switch (choice) {
            case 1 -> createPostMenu();
            case 2 -> manageMyPosts();
            case 3 -> browseAllPublicPosts();
            case 4 -> searchPostsByUser();
            case 5 -> showConnectionsFeed();  // NOW USED!
            default -> {}
        }
    }

    private void browseAllPublicPosts() {
        System.out.println("\n🌐 === ALL PUBLIC POSTS ===");
        System.out.println("Browse and interact with posts from everyone!");
        
        // Get all posts from the database
        List<Post> allPosts = postService.getAllPublicPosts();
        
        if (allPosts.isEmpty()) {
            System.out.println("📭 No posts available yet!");
            System.out.println("💡 Be the first to post something!");
            System.out.println("\n⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        // Sort by date (newest first)
        allPosts.sort((p1, p2) -> {
            if (p1.getCreatedAt() != null && p2.getCreatedAt() != null) {
                return p2.getCreatedAt().compareTo(p1.getCreatedAt());
            }
            return 0;
        });
        
        // Display ALL posts with full content
        System.out.println("\n📊 TOTAL POSTS: " + allPosts.size());
        System.out.println("=".repeat(80));
        
        for (Post post : allPosts) {
            // Get author info
            User author = userService.getUserById(post.getUserId());
            String authorName = (author != null) ? "@" + author.getUsername() : "User#" + post.getUserId();
            
            // Get stats
            int likeCount = likeService.getLikeCount(post.getId());
            int commentCount = commentService.getCommentCount(post.getId());
            boolean iLiked = likeService.isLikedByUser(post.getId(), currentUser.getId());
            boolean isMyPost = post.getUserId() == currentUser.getId();  // This is now USED below
            
            System.out.println("\n" + "─".repeat(80));
            System.out.println("POST ID: " + post.getId());
            System.out.println("AUTHOR: " + authorName + (isMyPost ? " (YOU)" : ""));  // NOW USING isMyPost
            System.out.println("TIME: " + (post.getCreatedAt() != null ? post.getCreatedAt() : "Recently"));
            System.out.println("─".repeat(40));
            
            // Display full content
            System.out.println("CONTENT:");
            System.out.println(post.getContent());
            
            if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
                System.out.println("\n🏷️ TAGS: " + post.getHashtags());
            }
            
            System.out.println("\n📊 STATS: ❤️ " + likeCount + (iLiked ? " (You liked)" : "") + 
                             " | 💬 " + commentCount + " comments");
            
            // Show quick action indicators - NOW PROPERLY USING isMyPost
            if (isMyPost) {
                System.out.println("🤝 ✅ Your post");  // USING isMyPost
            } else {
                boolean connected = connectionService.areConnected(currentUser.getId(), post.getUserId());
                System.out.println("🤝 " + (connected ? "✅ Connected" : "🔗 Not connected"));
            }
            
            System.out.println("─".repeat(80));
        }
        
        // Post selection menu
        System.out.println("\n🎯 POST ACTIONS:");
        System.out.println("1. Select a post to interact (by ID)");
        System.out.println("2. Like/Unlike a post (by ID)");
        System.out.println("3. Comment on a post (by ID)");
        System.out.println("4. Refresh list");
        System.out.println("0. Back to Feed Menu");
        
        System.out.print("\nYour choice: ");
        int choice = InputUtil.getInt();
        
        switch (choice) {
            case 1 -> selectPostById(allPosts);
            case 2 -> quickLikePost(allPosts);
            case 3 -> quickCommentPost(allPosts);
            case 4 -> browseAllPublicPosts(); // Refresh
            default -> {}
        }
    }

    // ========== CONNECTIONS FEED (NOW INTEGRATED) ==========
    private void showConnectionsFeed() {
        System.out.println("\n🤝 === CONNECTIONS FEED ===");
        System.out.println("See what your connections are posting!");
        
        // Get connections feed
        List<Post> feed = postService.getConnectionsFeed(currentUser.getId());
        
        if (feed.isEmpty()) {
            System.out.println("📭 No posts from your connections yet!");
            System.out.println("💡 Connect with more people or your connections haven't posted.");
            System.out.println("\n⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        System.out.println("\n📊 TOTAL POSTS FROM CONNECTIONS: " + feed.size());
        System.out.println("=".repeat(80));
        
        for (Post post : feed) {
            User author = userService.getUserById(post.getUserId());
            String authorName = (author != null) ? "@" + author.getUsername() : "User#" + post.getUserId();
            
            int likeCount = likeService.getLikeCount(post.getId());
            int commentCount = commentService.getCommentCount(post.getId());
            boolean iLiked = likeService.isLikedByUser(post.getId(), currentUser.getId());
            boolean isMyPost = post.getUserId() == currentUser.getId();  // FIXED: Now used
            
            System.out.println("\n" + "─".repeat(80));
            System.out.println("POST ID: " + post.getId());
            System.out.println("AUTHOR: " + authorName + (isMyPost ? " (YOU)" : ""));  // USING isMyPost
            System.out.println("TIME: " + (post.getCreatedAt() != null ? post.getCreatedAt() : "Recently"));
            System.out.println("─".repeat(40));
            
            System.out.println("CONTENT:");
            System.out.println(post.getContent());
            
            if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
                System.out.println("\n🏷️ TAGS: " + post.getHashtags());
            }
            
            System.out.println("\n📊 STATS: ❤️ " + likeCount + (iLiked ? " (You liked)" : "") + 
                             " | 💬 " + commentCount + " comments");
            
            if (isMyPost) {
                System.out.println("🤝 ✅ Your post");  // USING isMyPost
            } else {
                System.out.println("🤝 ✅ Connected");
            }
            
            System.out.println("─".repeat(80));
        }
        
        // Post interaction menu
        System.out.println("\n🎯 POST ACTIONS:");
        System.out.println("1. Select a post to interact (by ID)");
        System.out.println("2. Like/Unlike a post (by ID)");
        System.out.println("3. Comment on a post (by ID)");
        System.out.println("4. Refresh feed");
        System.out.println("0. Back to Feed Menu");
        
        System.out.print("\nYour choice: ");
        int choice = InputUtil.getInt();
        
        switch (choice) {
            case 1 -> selectPostById(feed);
            case 2 -> quickLikePost(feed);
            case 3 -> quickCommentPost(feed);
            case 4 -> showConnectionsFeed(); // Refresh
            default -> {}
        }
    }

    private void selectPostById(List<Post> posts) {
        if (posts.isEmpty()) {
            System.out.println("❌ No posts available!");
            System.out.println("\n⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        System.out.print("\nEnter POST ID to interact: ");
        int postId = InputUtil.getInt();
        
        // Find the post by ID using enhanced search
        Post selectedPost = posts.stream()
            .filter(post -> post.getId() == postId)
            .findFirst()
            .orElse(null);
        
        if (selectedPost == null) {
            System.out.println("❌ Post ID " + postId + " not found!");
            System.out.println("Available Post IDs: ");
            for (int i = 0; i < Math.min(10, posts.size()); i++) {
                System.out.print(posts.get(i).getId() + " ");
                if ((i + 1) % 10 == 0) System.out.println();
            }
            if (posts.size() > 10) {
                System.out.println("\n... and " + (posts.size() - 10) + " more");
            }
            System.out.println();
        } else {
            System.out.println("✅ Found post! Opening interaction menu...");
            interactWithPost(selectedPost);
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    private void quickLikePost(List<Post> posts) {
        if (posts.isEmpty()) {
            System.out.println("❌ No posts available!");
            return;
        }
        
        System.out.print("\nEnter POST ID to like/unlike: ");
        int postId = InputUtil.getInt();
        
        // Find the post by ID
        Post selectedPost = posts.stream()
            .filter(post -> post.getId() == postId)
            .findFirst()
            .orElse(null);
        
        if (selectedPost == null) {
            System.out.println("❌ Post ID " + postId + " not found!");
            return;
        }
        
        // Check if already liked
        boolean alreadyLiked = likeService.isLikedByUser(postId, currentUser.getId());
        
        if (alreadyLiked) {
            likeService.unlikePost(postId, currentUser.getId());
            System.out.println("💔 Unliked post ID " + postId);
        } else {
            likeService.likePost(postId, currentUser.getId());
            System.out.println("❤️ Liked post ID " + postId);
            
            // Notify post owner if not yourself
            if (selectedPost.getUserId() != currentUser.getId()) {
                notificationService.notifyLikedPost(selectedPost.getUserId(), currentUser.getUsername());
            }
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
        // Return to the appropriate feed
        if (posts.get(0).getUserId() == currentUser.getId()) {
            manageMyPosts();
        } else {
            browseAllPublicPosts();
        }
    }

    private void quickCommentPost(List<Post> posts) {
        if (posts.isEmpty()) {
            System.out.println("❌ No posts available!");
            return;
        }
        
        System.out.print("\nEnter POST ID to comment on: ");
        int postId = InputUtil.getInt();
        
        // Find the post by ID
        Post selectedPost = posts.stream()
            .filter(post -> post.getId() == postId)
            .findFirst()
            .orElse(null);
        
        if (selectedPost == null) {
            System.out.println("❌ Post ID " + postId + " not found!");
            return;
        }
        
        System.out.print("💬 Your comment: ");
        String content = InputUtil.getString();
        
        if (content.trim().isEmpty()) {
            System.out.println("❌ Comment cannot be empty!");
        } else {
            commentService.addComment(postId, currentUser.getId(), content);
            System.out.println("✅ Comment added to post ID " + postId);
            
            // Notify post owner if not yourself
            if (selectedPost.getUserId() != currentUser.getId()) {
                notificationService.notifyNewComment(selectedPost.getUserId(), currentUser.getUsername());
            }
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
        // Return to the appropriate feed
        if (posts.get(0).getUserId() == currentUser.getId()) {
            manageMyPosts();
        } else {
            browseAllPublicPosts();
        }
    }

    private void searchPostsByUser() {
        System.out.println("\n🔍 === SEARCH POSTS BY USER ===");
        System.out.print("Enter username: ");
        String username = InputUtil.getString();
        
        // Find the user
        User targetUser = userService.getUserByUsername(username);
        if (targetUser == null) {
            System.out.println("❌ User @" + username + " not found!");
            System.out.println("\n⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        System.out.println("\n📝 POSTS BY @" + targetUser.getUsername() + ":");
        System.out.println("=".repeat(80));
        
        // Get user's posts
        List<Post> userPosts = postService.getUserFeed(targetUser.getId());
        
        if (userPosts.isEmpty()) {
            System.out.println("📭 @" + targetUser.getUsername() + " hasn't posted anything yet.");
        } else {
            // Sort by date (newest first)
            userPosts.sort((p1, p2) -> {
                if (p1.getCreatedAt() != null && p2.getCreatedAt() != null) {
                    return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                }
                return 0;
            });
            
            // Display all posts with full content
            for (Post post : userPosts) {
                // Get stats
                int likeCount = likeService.getLikeCount(post.getId());
                int commentCount = commentService.getCommentCount(post.getId());
                boolean iLiked = likeService.isLikedByUser(post.getId(), currentUser.getId());
                boolean isMyPost = post.getUserId() == currentUser.getId();  // FIXED: Now used
                
                System.out.println("\n" + "─".repeat(80));
                System.out.println("POST ID: " + post.getId());
                System.out.println("AUTHOR: @" + targetUser.getUsername() + (isMyPost ? " (YOU)" : ""));  // USING isMyPost
                System.out.println("TIME: " + (post.getCreatedAt() != null ? post.getCreatedAt() : "Recently"));
                System.out.println("─".repeat(40));
                
                // Display full content
                System.out.println("CONTENT:");
                System.out.println(post.getContent());
                
                if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
                    System.out.println("\n🏷️ TAGS: " + post.getHashtags());
                }
                
                System.out.println("\n📊 STATS: ❤️ " + likeCount + (iLiked ? " (You liked)" : "") + 
                                 " | 💬 " + commentCount + " comments");
                System.out.println("─".repeat(80));
            }
            
            System.out.println("\n🎯 ACTIONS:");
            System.out.println("1. Select a post to interact (by ID)");
            System.out.println("2. View user profile");
            if (targetUser.getId() != currentUser.getId() && targetUser.getUserType().equals("PERSONAL")) {
                boolean connected = connectionService.areConnected(currentUser.getId(), targetUser.getId());
                if (!connected) {
                    System.out.println("3. Send connection request");
                }
            }
            System.out.println("0. Back");
            
            System.out.print("\nYour choice: ");
            int choice = InputUtil.getInt();
            
            switch (choice) {
                case 1 -> selectPostById(userPosts);
                case 2 -> {
                    System.out.println("\n👤 USER PROFILE:");
                    System.out.println("Username: @" + targetUser.getUsername());
                    System.out.println("Email: " + targetUser.getEmail());
                    System.out.println("Account Type: " + targetUser.getUserType());
                    System.out.println("Total Posts: " + userPosts.size());
                    
                    boolean connected = connectionService.areConnected(currentUser.getId(), targetUser.getId());
                    System.out.println("Connection: " + (connected ? "✅ Connected" : "🔗 Not connected"));
                    
                    System.out.println("\n⏎ Press Enter...");
                    InputUtil.getString();
                }
                case 3 -> {
                    if (targetUser.getId() != currentUser.getId() && targetUser.getUserType().equals("PERSONAL")) {
                        boolean connected = connectionService.areConnected(currentUser.getId(), targetUser.getId());
                        if (!connected) {
                            System.out.print("Send connection request to @" + targetUser.getUsername() + "? (y/n): ");
                            if (InputUtil.getString().equalsIgnoreCase("y")) {
                                boolean success = connectionService.sendRequest(currentUser.getId(), targetUser.getId());
                                if (success) {
                                    System.out.println("✅ Connection request sent!");
                                }
                            }
                        }
                    }
                }
                default -> {}
            }
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    private void createPostMenu() {
        System.out.print("💭 What's happening? "); 
        String content = InputUtil.getString();
        System.out.print("🏷️ Hashtags (optional): "); 
        String hashtags = InputUtil.getString();
        
        Post post = new Post();
        post.setUserId(currentUser.getId());
        post.setContent(content);
        post.setHashtags(hashtags);
        postService.createPost(post);
        System.out.println("✅ Posted successfully!");
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }

    private void manageMyPosts() {
        List<Post> myPosts = postService.getUserFeed(currentUser.getId());
        if (myPosts.isEmpty()) {
            System.out.println("📱 No posts yet! Create one above."); 
            return;
        }

        while (true) {
            System.out.println("\n📱 MY POSTS:");
            for (int i = 0; i < myPosts.size(); i++) {
                Post p = myPosts.get(i);
                int likeCount = likeService.getLikeCount(p.getId());
                int commentCount = commentService.getCommentCount(p.getId());
                String preview = safePreview(p.getContent());
                System.out.println((i+1) + ". " + preview + 
                    " ❤️(" + likeCount + ") 💬(" + commentCount + ")");
            }

            System.out.print("Select post (0=Back): ");
            int index = InputUtil.getInt() - 1;
            if (index < 0) break;
            if (index >= 0 && index < myPosts.size()) {
                interactWithPost(myPosts.get(index));
            }
        }
    }

    private void interactWithPost(Post post) {
        while (true) {
            int likes = likeService.getLikeCount(post.getId());
            int comments = commentService.getCommentCount(post.getId());
            boolean iLiked = likeService.isLikedByUser(post.getId(), currentUser.getId());

            System.out.println("\n═══════════════════════════════════════");
            System.out.println("💬 POST ID: " + post.getId());
            System.out.println("Content: " + post.getContent());
            if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
                System.out.println("🏷️ Tags: " + post.getHashtags());
            }
            System.out.println("\n📊 STATS:");
            System.out.println("❤️ Likes: " + likes + (iLiked ? " (YOU LIKED)" : ""));
            System.out.println("💬 Comments: " + comments);
            System.out.println("═══════════════════════════════════════");
            
            System.out.println("\n1. " + (iLiked ? "💔 Unlike" : "❤️ Like"));
            System.out.println("2. 💬 Add Comment");
            System.out.println("3. 👁️ View Comments");
            System.out.println("4. 🗑️ Delete My Comment");
            System.out.println("5. 🔄 Share/Repost");
            System.out.println("6. ✏️ Edit Post");
            System.out.println("7. 🗑️ Delete Post");
            System.out.println("0. Back");

            int choice = InputUtil.getInt();
            switch (choice) {
                case 1 -> toggleLike(post, iLiked);
                case 2 -> addComment(post);
                case 3 -> viewComments(post);
                case 4 -> deleteMyComment(post);
                case 5 -> sharePost(post);
                case 6 -> editPost(post);
                case 7 -> {
                    deletePost(post);
                    return;
                }
                default -> {
                    return;
                }
            }
        }
    }

    private void toggleLike(Post post, boolean alreadyLiked) {
        if (alreadyLiked) {
            likeService.unlikePost(post.getId(), currentUser.getId());
            System.out.println("💔 Unliked!");
        } else {
            likeService.likePost(post.getId(), currentUser.getId());
            System.out.println("❤️ Liked!");
            notificationService.notifyLikedPost(post.getUserId(), currentUser.getUsername());
        }
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }

    private void addComment(Post post) {
        System.out.print("💬 Your comment: ");
        String content = InputUtil.getString();
        if (content.trim().isEmpty()) {
            System.out.println("❌ Comment cannot be empty!");
            return;
        }
        commentService.addComment(post.getId(), currentUser.getId(), content);
        System.out.println("✅ Comment added!");
        notificationService.notifyNewComment(post.getUserId(), currentUser.getUsername());
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }

    private void viewComments(Post post) {
        List<Comment> comments = commentService.getComments(post.getId());
        if (comments.isEmpty()) {
            System.out.println("💬 No comments yet!");
        } else {
            System.out.println("\n💬 COMMENTS (" + comments.size() + "):");
            for (int i = 0; i < comments.size(); i++) {
                Comment c = comments.get(i);
                String username = c.getUsername() != null ? c.getUsername() : "User#" + c.getUserId();
                System.out.println((i+1) + ". 👤 " + username + ": " + c.getContent());
                if (c.getUserId() == currentUser.getId()) {
                    System.out.println("   👆 Your comment (ID: " + c.getId() + ")");
                }
            }
        }
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }

    private void deleteMyComment(Post post) {
        List<Comment> myComments = commentService.getMyComments(post.getId(), currentUser.getId());
        if (myComments.isEmpty()) {
            System.out.println("❌ You have no comments on this post.");
            return;
        }

        System.out.println("\n🗑️ YOUR COMMENTS ON THIS POST:");
        for (int i = 0; i < myComments.size(); i++) {
            Comment c = myComments.get(i);
            System.out.println((i+1) + ". " + c.getContent());
        }

        System.out.print("Select comment to delete (0=Cancel): ");
        int index = InputUtil.getInt() - 1;
        if (index >= 0 && index < myComments.size()) {
            commentService.deleteComment(myComments.get(index).getId());
            System.out.println("✅ Comment deleted!");
        }
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }

    private void sharePost(Post post) {
        System.out.println("\n🔄 SHARE POST");
        System.out.println("Original post by User ID: " + post.getUserId());
        System.out.println("Content: " + safePreview(post.getContent()));
        
        System.out.print("Add your message (optional): ");
        String message = InputUtil.getString();
        
        // Create a new post with attribution
        String shareContent = "[SHARED] " + (message.isEmpty() ? "" : message + "\n\n") + 
                              "Original: " + post.getContent();
        Post sharePost = new Post();
        sharePost.setUserId(currentUser.getId());
        sharePost.setContent(shareContent);
        sharePost.setHashtags(post.getHashtags());
        postService.createPost(sharePost);
        
        System.out.println("✅ Post shared on your profile!");
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }

    private void editPost(Post post) {
        System.out.print("New content: "); 
        String content = InputUtil.getString();
        System.out.print("New hashtags: "); 
        String hashtags = InputUtil.getString();
        postService.editPost(post.getId(), content, hashtags);
        System.out.println("✅ Post updated!");
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }

    private void deletePost(Post post) {
        System.out.print("Are you sure? (y/n): ");
        if (InputUtil.getString().equalsIgnoreCase("y")) {
            postService.deletePost(post.getId());
            System.out.println("✅ Post deleted permanently!");
        } else {
            System.out.println("Deletion cancelled.");
        }
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }

    // CONNECTIONS MENU
    private void showConnectionsMenu() {
        System.out.println("\n👥 === CONNECTIONS ===");
        System.out.println("1. 🔍 Find Users");
        System.out.println("2. 📥 Incoming Requests");
        System.out.println("3. ✅ Accept Requests");
        System.out.println("4. 👥 My Connections");
        System.out.println("0. Back");
        
        int choice = InputUtil.getInt();
        switch (choice) {
            case 1 -> findAndConnectUsers();
            case 2 -> showIncomingRequests();
            case 3 -> acceptConnectionRequest();
            case 4 -> showMyConnections();
            default -> {}
        }
    }

    private void findAndConnectUsers() {
        System.out.print("\n🔍 Search username: ");
        String search = InputUtil.getString();
        
        List<User> users = userService.searchUsers(search);
        if (users.isEmpty() || (users.size() == 1 && users.get(0).getId() == currentUser.getId())) {
            System.out.println("❌ No other users found!");
        } else {
            System.out.println("\n👥 FOUND USERS:");
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                if (u.getId() != currentUser.getId()) {
                    String status = getConnectionStatusString(currentUser.getId(), u.getId());
                    System.out.println((i+1) + ". @" + u.getUsername() + 
                                     " (" + u.getUserType() + ") - " + status);
                }
            }
            
            System.out.print("\nSelect user number to connect (0=Cancel): ");
            int userChoice = InputUtil.getInt();
            if (userChoice > 0 && userChoice <= users.size()) {
                User targetUser = users.get(userChoice - 1);
                if (targetUser.getId() != currentUser.getId()) {
                    boolean success = connectionService.sendRequest(currentUser.getId(), targetUser.getId());
                    if (success) {
                        System.out.println("✅ Connection request sent to @" + targetUser.getUsername() + "!");
                    }
                }
            }
        }
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    private void showIncomingRequests() {
        System.out.println("\n📥 INCOMING REQUESTS:");
        System.out.println("=".repeat(60));
        
        List<Connection> requests = connectionService.getPendingRequests(currentUser.getId());
        
        if (requests.isEmpty()) {
            System.out.println("✅ No pending requests!");
        } else {
            System.out.println("You have " + requests.size() + " pending request(s):");
            System.out.println("-".repeat(60));
            
            for (int i = 0; i < requests.size(); i++) {
                Connection request = requests.get(i);
                User sender = userService.getUserById(request.getUser1Id());
                if (sender != null) {
                    System.out.println((i+1) + ". 👤 From: @" + sender.getUsername());
                    System.out.println("   📧 Email: " + sender.getEmail());
                    System.out.println("   🏷️ Type: " + sender.getUserType());
                    System.out.println("   📅 Sent: " + request.getCreatedAt());
                    System.out.println("   🆔 Request ID: " + request.getId());
                    System.out.println("   " + "-".repeat(40));
                }
            }
            
            System.out.println("\n🎯 ACTIONS:");
            System.out.println("1. ✅ Accept a Request");
            System.out.println("2. ❌ Reject a Request");
            System.out.println("0. ↩️ Back");
            
            System.out.print("\nYour choice: ");
            int choice = InputUtil.getInt();
            
            switch (choice) {
                case 1 -> acceptIncomingRequest(requests);
                case 2 -> rejectIncomingRequest(requests);
                default -> {}
            }
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    private void acceptIncomingRequest(List<Connection> requests) {
        if (requests.isEmpty()) return;
        
        System.out.print("Enter request number to accept: ");
        int index = InputUtil.getInt() - 1;
        
        if (index >= 0 && index < requests.size()) {
            Connection request = requests.get(index);
            boolean success = connectionService.acceptRequest(request.getId());
            if (success) {
                User sender = userService.getUserById(request.getUser1Id());
                System.out.println("✅ Connection request from @" + sender.getUsername() + " accepted!");
                System.out.println("👥 You are now connected!");
            }
        } else {
            System.out.println("❌ Invalid selection!");
        }
    }

    private void rejectIncomingRequest(List<Connection> requests) {
        if (requests.isEmpty()) return;
        
        System.out.print("Enter request number to reject: ");
        int index = InputUtil.getInt() - 1;
        
        if (index >= 0 && index < requests.size()) {
            Connection request = requests.get(index);
            User sender = userService.getUserById(request.getUser1Id());
            
            System.out.print("Are you sure you want to reject request from @" + 
                           sender.getUsername() + "? (yes/no): ");
            String confirm = InputUtil.getString().toLowerCase();
            
            if (confirm.equals("yes") || confirm.equals("y")) {
                boolean success = connectionService.rejectRequest(currentUser.getId(), sender.getId());
                if (success) {
                    System.out.println("✅ Request from @" + sender.getUsername() + " rejected.");
                }
            } else {
                System.out.println("❌ Rejection cancelled.");
            }
        } else {
            System.out.println("❌ Invalid selection!");
        }
    }

    private void acceptConnectionRequest() {
        System.out.println("\n✅ ACCEPT REQUESTS BY USERNAME");
        System.out.print("Enter sender's username: ");
        String senderUsername = InputUtil.getString();
        
        boolean success = connectionService.acceptRequestByUsername(currentUser.getId(), senderUsername);
        if (success) {
            System.out.println("✅ Connection accepted!");
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    // UPDATED: Proper My Connections implementation
    private void showMyConnections() {
        System.out.println("\n👥 MY CONNECTIONS:");
        System.out.println("=".repeat(60));
        
        List<User> connections = connectionService.getConnections(currentUser.getId());
        
        if (connections.isEmpty()) {
            System.out.println("❌ You don't have any connections yet!");
            System.out.println("💡 Try 'Find Users' to connect with people.");
        } else {
            System.out.println("✅ You have " + connections.size() + " connection(s)");
            System.out.println("-".repeat(60));
            
            // Display all connections with details
            for (int i = 0; i < connections.size(); i++) {
                User connection = connections.get(i);
                
                // Get mutual friends count
                List<User> mutuals = connectionService.getMutualConnections(
                    currentUser.getId(), connection.getId());
                
                System.out.println("\n" + (i + 1) + ". 👤 " + connection.getUsername());
                System.out.println("   📧 Email: " + connection.getEmail());
                System.out.println("   🏷️ Type: " + connection.getUserType());
                System.out.println("   🤝 Mutual Friends: " + mutuals.size());
                System.out.println("   🆔 User ID: " + connection.getId());
                
                // Show mutual friends if any
                if (!mutuals.isEmpty()) {
                    System.out.print("   👥 Mutual: ");
                    for (int j = 0; j < Math.min(3, mutuals.size()); j++) {
                        System.out.print("@" + mutuals.get(j).getUsername());
                        if (j < Math.min(3, mutuals.size()) - 1) {
                            System.out.print(", ");
                        }
                    }
                    if (mutuals.size() > 3) {
                        System.out.print(" and " + (mutuals.size() - 3) + " more");
                    }
                    System.out.println();
                }
                System.out.println("   " + "-".repeat(40));
            }
            
            // Show connection statistics
            showConnectionStats();
            
            // Add interaction menu
            System.out.println("\n🎯 CONNECTION ACTIONS:");
            System.out.println("1. 🔍 View Connection Details");
            System.out.println("2. 🗑️ Remove Connection");
            System.out.println("3. 🔄 Refresh List");
            System.out.println("0. ↩️ Back");
            
            System.out.print("\nYour choice: ");
            int choice = InputUtil.getInt();
            
            switch (choice) {
                case 1 -> viewConnectionDetails(connections);
                case 2 -> removeConnection(connections);
                case 3 -> showMyConnections(); // Refresh
                default -> {}
            }
        }
        
        System.out.println("\n⏎ Press Enter to continue...");
        InputUtil.getString();
    }

    private void viewConnectionDetails(List<User> connections) {
        if (connections.isEmpty()) return;
        
        System.out.print("\nEnter connection number to view details: ");
        int index = InputUtil.getInt() - 1;
        
        if (index >= 0 && index < connections.size()) {
            User connection = connections.get(index);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("👤 CONNECTION DETAILS");
            System.out.println("=".repeat(60));
            
            System.out.println("Username: @" + connection.getUsername());
            System.out.println("Email: " + connection.getEmail());
            System.out.println("Account Type: " + connection.getUserType());
            System.out.println("User ID: " + connection.getId());
            
            // Get mutual connections
            List<User> mutuals = connectionService.getMutualConnections(
                currentUser.getId(), connection.getId());
            
            System.out.println("\n🤝 MUTUAL CONNECTIONS (" + mutuals.size() + "):");
            if (mutuals.isEmpty()) {
                System.out.println("   No mutual connections yet.");
            } else {
                for (int i = 0; i < Math.min(10, mutuals.size()); i++) {
                    User mutual = mutuals.get(i);
                    System.out.println("   " + (i + 1) + ". @" + mutual.getUsername() + 
                                     " (" + mutual.getUserType() + ")");
                }
                if (mutuals.size() > 10) {
                    System.out.println("   ... and " + (mutuals.size() - 10) + " more");
                }
            }
            
            // Get connection strength
            double strength = calculateConnectionStrength(mutuals.size());
            System.out.printf("\n💪 Connection Strength: %.0f%%%n", strength * 100);
            
            if (strength > 0.7) {
                System.out.println("🌟 Strong connection! Lots of mutual friends.");
            } else if (strength > 0.3) {
                System.out.println("👍 Good connection! Some common connections.");
            } else {
                System.out.println("💡 New connection! Connect with more mutual friends.");
            }
            
            System.out.println("\n⏎ Press Enter to continue...");
            InputUtil.getString();
        } else {
            System.out.println("❌ Invalid selection!");
        }
    }

    private double calculateConnectionStrength(int mutualCount) {
        // Simple strength calculation based on mutual friends
        if (mutualCount >= 10) return 1.0;
        if (mutualCount >= 5) return 0.7;
        if (mutualCount >= 3) return 0.5;
        if (mutualCount >= 1) return 0.3;
        return 0.1;
    }

    private void removeConnection(List<User> connections) {
        if (connections.isEmpty()) return;
        
        System.out.print("\nEnter connection number to remove: ");
        int index = InputUtil.getInt() - 1;
        
        if (index >= 0 && index < connections.size()) {
            User connection = connections.get(index);
            
            System.out.println("\n⚠️ WARNING: Removing Connection");
            System.out.println("You're about to remove @" + connection.getUsername());
            System.out.println("This action cannot be undone!");
            System.out.print("Are you sure? (yes/no): ");
            
            String confirmation = InputUtil.getString().toLowerCase();
            if (confirmation.equals("yes") || confirmation.equals("y")) {
                boolean success = connectionService.removeConnection(
                    currentUser.getId(), connection.getId());
                if (success) {
                    System.out.println("✅ Connection removed successfully!");
                    System.out.println("👤 @" + connection.getUsername() + " is no longer in your connections.");
                    
                    // Refresh the list
                    showMyConnections();
                } else {
                    System.out.println("❌ Failed to remove connection!");
                }
            } else {
                System.out.println("❌ Removal cancelled.");
            }
        } else {
            System.out.println("❌ Invalid selection!");
        }
        
        System.out.println("\n⏎ Press Enter to continue...");
        InputUtil.getString();
    }

    // ANALYTICS MENU
    private void showAnalyticsMenu() {
        System.out.println("\n📊 ANALYTICS & INSIGHTS");
        System.out.println("=".repeat(40));
        System.out.println("1. 📈 Network Growth");
        System.out.println("2. 🤝 Connection Insights");
        System.out.println("3. 👥 Top Connections");
        System.out.println("4. 💡 Recommendations");
        System.out.println("0. ↩️ Back to Dashboard");
        System.out.print("\nYour choice: ");
        
        int choice = InputUtil.getInt();
        switch (choice) {
            case 1 -> showNetworkGrowth();
            case 2 -> showConnectionInsights();
            case 3 -> showTopConnections();
            case 4 -> showRecommendations();
            default -> {}
        }
    }

    private void showNetworkGrowth() {
        System.out.println("\n📈 NETWORK GROWTH");
        System.out.println("=".repeat(40));
        
        int totalConnections = connectionService.getConnectionCount(currentUser.getId());
        int pendingReceived = connectionService.getPendingRequestsCount(currentUser.getId());
        int pendingSent = connectionService.getSentRequestsCount(currentUser.getId());
        
        System.out.println("✅ Current Connections: " + totalConnections);
        System.out.println("📥 Pending Requests (incoming): " + pendingReceived);
        System.out.println("📤 Pending Requests (outgoing): " + pendingSent);
        System.out.println("📊 Total Network Size: " + (totalConnections + pendingReceived + pendingSent));
        
        // Calculate network health
        double healthScore = (double) totalConnections / (totalConnections + pendingReceived + pendingSent + 1) * 100;
        System.out.printf("🏆 Network Health Score: %.1f%%\n", healthScore);
        
        if (healthScore > 80) {
            System.out.println("💪 Excellent network health!");
        } else if (healthScore > 60) {
            System.out.println("👍 Good network, keep growing!");
        } else {
            System.out.println("💡 Try to convert more pending requests to connections!");
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    private void showConnectionInsights() {
        System.out.println("\n🤝 CONNECTION INSIGHTS");
        System.out.println("=".repeat(40));
        
        List<User> connections = connectionService.getConnections(currentUser.getId());
        
        if (connections.isEmpty()) {
            System.out.println("No connections to analyze yet.");
        } else {
            // Count user types
            int personal = 0, creator = 0, business = 0;
            for (User conn : connections) {
                switch (conn.getUserType()) {
                    case "PERSONAL": personal++; break;
                    case "CREATOR": creator++; break;
                    case "BUSINESS": business++; break;
                }
            }
            
            System.out.println("👤 Connection Types:");
            System.out.println("   Personal Users: " + personal);
            System.out.println("   Creators: " + creator);
            System.out.println("   Businesses: " + business);
            
            // Average mutual connections
            int totalMutuals = 0;
            for (User conn : connections) {
                totalMutuals += connectionService.getMutualConnections(currentUser.getId(), conn.getId()).size();
            }
            double avgMutuals = connections.size() > 0 ? (double) totalMutuals / connections.size() : 0;
            System.out.printf("🤝 Average Mutual Connections: %.1f\n", avgMutuals);
            
            // Most connected friend
            User mostConnected = null;
            int maxMutuals = 0;
            for (User conn : connections) {
                int mutuals = connectionService.getMutualConnections(currentUser.getId(), conn.getId()).size();
                if (mutuals > maxMutuals) {
                    maxMutuals = mutuals;
                    mostConnected = conn;
                }
            }
            
            if (mostConnected != null) {
                System.out.println("👑 Most Connected: @" + mostConnected.getUsername() + 
                                 " (" + maxMutuals + " mutual friends)");
            }
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    private void showTopConnections() {
        System.out.println("\n👥 TOP CONNECTIONS");
        System.out.println("=".repeat(40));
        
        List<User> connections = connectionService.getConnections(currentUser.getId());
        
        if (connections.isEmpty()) {
            System.out.println("No connections yet.");
        } else {
            // Sort by mutual connections
            List<User> sortedConnections = new ArrayList<>(connections);
            sortedConnections.sort((u1, u2) -> {
                int mut1 = connectionService.getMutualConnections(currentUser.getId(), u1.getId()).size();
                int mut2 = connectionService.getMutualConnections(currentUser.getId(), u2.getId()).size();
                return Integer.compare(mut2, mut1); // Descending order
            });
            
            System.out.println("Ranked by mutual connections:");
            System.out.println("-".repeat(40));
            
            for (int i = 0; i < Math.min(10, sortedConnections.size()); i++) {
                User conn = sortedConnections.get(i);
                int mutuals = connectionService.getMutualConnections(currentUser.getId(), conn.getId()).size();
                System.out.printf("%2d. @%-15s Mutual: %d\n", (i+1), conn.getUsername(), mutuals);
            }
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    private void showRecommendations() {
        System.out.println("\n💡 RECOMMENDATIONS");
        System.out.println("=".repeat(40));
        
        List<User> suggestions = connectionService.getConnectionSuggestions(currentUser.getId());
        
        if (suggestions.isEmpty()) {
            System.out.println("1. 🤝 Connect with more people to get better recommendations!");
            System.out.println("2. 📝 Complete your profile to attract more connections");
            System.out.println("3. 🔄 Engage with posts to increase visibility");
        } else {
            System.out.println("Top 5 connection recommendations:");
            System.out.println("-".repeat(40));
            
            for (int i = 0; i < Math.min(5, suggestions.size()); i++) {
                User user = suggestions.get(i);
                int mutuals = connectionService.getMutualConnections(currentUser.getId(), user.getId()).size();
                System.out.printf("%d. @%-15s %d mutual friends\n", (i+1), user.getUsername(), mutuals);
            }
            
            System.out.println("\n💡 TIPS:");
            System.out.println("• Connect with people who have many mutual friends");
            System.out.println("• Engage with content from your connections' connections");
            System.out.println("• Join groups or communities related to your interests");
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    // KEEP EXISTING PROFILE METHODS BUT UPDATE editProfile() to include privacy
    private void editProfile() {
        System.out.println("\n📝 === EDIT PROFILE ===");
        System.out.println("Enter new values (press Enter to keep current):");
        
        System.out.print("Full Name: "); 
        String name = InputUtil.getString();
        if (!name.isEmpty()) profileName = name;
        
        System.out.print("Bio/About: "); 
        String bio = InputUtil.getString();
        if (!bio.isEmpty()) profileBio = bio;
        
        System.out.print("Location: "); 
        String location = InputUtil.getString();
        if (!location.isEmpty()) profileLocation = location;
        
        System.out.print("Website: "); 
        String website = InputUtil.getString();
        if (!website.isEmpty()) profileWebsite = website;
        
        // Privacy setting in profile edit too
        System.out.print("Privacy (PUBLIC/PRIVATE) [Current: " + profilePrivacy + "]: "); 
        String privacy = InputUtil.getString();
        if (!privacy.isEmpty() && (privacy.equalsIgnoreCase("PUBLIC") || privacy.equalsIgnoreCase("PRIVATE"))) {
            profilePrivacy = privacy.toUpperCase();
        }
        
        System.out.println("\n✅ Profile updated successfully!");
        System.out.println("📝 SUMMARY:");
        System.out.println("👤 Name: " + (profileName.isEmpty() ? "Not set" : profileName));
        System.out.println("📄 Bio: " + (profileBio.isEmpty() ? "Not set" : safePreview(profileBio)));
        System.out.println("📍 Location: " + (profileLocation.isEmpty() ? "Not set" : profileLocation));
        System.out.println("🌐 Website: " + (profileWebsite.isEmpty() ? "Not set" : profileWebsite));
        System.out.println("🔒 Privacy: " + profilePrivacy);
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    private void viewMyInfo() {
        System.out.println("\n👁️ === MY COMPLETE PROFILE ===");
        System.out.println("👤 Username: " + currentUser.getUsername());
        System.out.println("📧 Email: " + currentUser.getEmail());
        System.out.println("📊 Account Type: " + currentUser.getUserType());
        System.out.println("🆔 User ID: " + currentUser.getId());
        
        System.out.println("\n📝 SAVED PROFILE:");
        System.out.println("👤 Full Name: " + (profileName.isEmpty() ? "Not set" : profileName));
        System.out.println("📄 Bio: " + (profileBio.isEmpty() ? "Not set" : safePreview(profileBio)));
        System.out.println("📍 Location: " + (profileLocation.isEmpty() ? "Not set" : profileLocation));
        System.out.println("🌐 Website: " + (profileWebsite.isEmpty() ? "Not set" : profileWebsite));
        System.out.println("🔒 Privacy: " + profilePrivacy);
        
        System.out.println("\n🔐 SECURITY STATUS:");
        System.out.println("Security Question: " + 
            (currentUser.getSecurityQuestion() != null && !currentUser.getSecurityQuestion().isEmpty() ? 
             "Set" : "Not set"));
        System.out.println("Login Attempts: " + loginAttempts + " (Recent)");
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    // ========== HELPER METHODS ==========
    
    private void showConnectionStats() {
        int totalConnections = connectionService.getConnectionCount(currentUser.getId());
        int pendingReceived = connectionService.getPendingRequestsCount(currentUser.getId());
        int pendingSent = connectionService.getSentRequestsCount(currentUser.getId());
        
        System.out.println("📊 NETWORK STATS: Connections: " + totalConnections + 
                         " | 📥 Pending: " + pendingReceived + 
                         " | 📤 Sent: " + pendingSent);
        System.out.println("-".repeat(50));
    }

    private String getConnectionStatusString(int userId1, int userId2) {
        String status = connectionService.getRelationshipStatus(userId1, userId2);
        switch (status) {
            case "NO_RELATIONSHIP": return "Not connected";
            case "REQUEST_SENT": return "Request sent";
            case "REQUEST_RECEIVED": return "Request received";
            case "ACCEPTED": return "Connected ✓";
            case "REJECTED": return "Rejected";
            default: return status;
        }
    }

    private String safePreview(String content) {
        if (content == null || content.length() <= 50) return content;
        return content.substring(0, 50) + "...";
    }

    private void searchUsers() {
        System.out.print("🔍 Search users by name/username: ");
        String query = InputUtil.getString();
        List<User> users = userService.searchUsers(query);
        
        if (users.isEmpty()) {
            System.out.println("❌ No users found!");
        } else {
            System.out.println("\n👥 USERS FOUND:");
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                if (u.getId() != currentUser.getId()) {
                    String status = getConnectionStatusString(currentUser.getId(), u.getId());
                    System.out.println((i+1) + ". @" + u.getUsername() + 
                                     " (" + u.getUserType() + ") - " + status);
                }
            }
            
            // Add connection option
            System.out.print("\nEnter user number to connect (0=Back): ");
            int choice = InputUtil.getInt();
            if (choice > 0 && choice <= users.size()) {
                User targetUser = users.get(choice - 1);
                if (targetUser.getId() != currentUser.getId()) {
                    boolean success = connectionService.sendRequest(currentUser.getId(), targetUser.getId());
                    if (success) {
                        System.out.println("✅ Connection request sent to @" + targetUser.getUsername() + "!");
                    }
                }
            }
        }
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
} // End of MainMenu class