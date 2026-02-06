package com.revconnectapp.ui;

import com.revconnectapp.model.User;
import com.revconnectapp.service.UserService;
import com.revconnectapp.service.ConnectionService;
import com.revconnectapp.service.NotificationService;
import com.revconnectapp.util.InputUtil;
import java.util.List;

public class MainMenu {
    private UserService userService = new UserService();
    private NotificationService notificationService = new NotificationService();
    private ConnectionService connectionService = new ConnectionService();
    private User currentUser;

    // Profile storage
    private String profileName = "", profileBio = "", profileLocation = "", profileWebsite = "", profilePrivacy = "PUBLIC";
    
    // Security storage
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
            
            if (securityQuestion.isEmpty()) {
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
            case "📱 Feed" -> {
                FeedMenu feedMenu = new FeedMenu(currentUser);
                feedMenu.show();
            }
            case "🔔 Notifications" -> new NotificationMenu().show(currentUser);
            case "👥 Connections" -> {
                // Use the new ConnectionsMenu class
                ConnectionsMenu connectionsMenu = new ConnectionsMenu(currentUser, userService, connectionService);
                connectionsMenu.show();
            }
            case "📊 Analytics" -> showAnalyticsMenu();
            case "🔐 Security" -> {
                try {
                    // Create SecurityMenu with current privacy setting
                    SecurityMenu securityMenu = new SecurityMenu(currentUser, userService, 
                            securityQuestion, securityAnswer, loginAttempts, profilePrivacy);
                    securityMenu.show();
                    
                    // Update all values after security menu returns
                    securityQuestion = securityMenu.getSecurityQuestion();
                    securityAnswer = securityMenu.getSecurityAnswer();
                    loginAttempts = securityMenu.getLoginAttempts();
                    profilePrivacy = securityMenu.getProfilePrivacy(); // Get updated privacy
                } catch (SecurityException e) {
                    // Password changed or reset - logout required
                    System.out.println("\n" + e.getMessage());
                    System.out.println("⏎ Press Enter to continue...");
                    InputUtil.getString();
                    
                    // Log out
                    currentUser = null;
                    loginAttempts = 0;
                    start();
                    return;
                }
            }
            default -> { 
                currentUser = null; 
                loginAttempts = 0; // Reset login attempts on logout
                System.out.println("👋 Goodbye!");
                start(); 
            }
        }
        showDashboard();
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
        
        // Check security question - using local variable
        if (securityQuestion == null || securityQuestion.isEmpty()) {
            System.out.println("❌ Security question not set for this account!");
            System.out.println("⚠️ There are 2 possible issues:");
            System.out.println("   1. You haven't set a security question yet");
            System.out.println("   2. Security questions aren't stored in database yet");
            System.out.println("\n💡 Quick fix: Set security question now? (y/n): ");
            
            String choice = InputUtil.getString().toLowerCase();
            if (choice.equals("y") || choice.equals("yes")) {
                // Temporarily set security question
                System.out.print("Enter a security question: ");
                String question = InputUtil.getString();
                System.out.print("Answer: ");
                String answer = InputUtil.getString();
                
                // Store locally
                securityQuestion = question;
                securityAnswer = answer.toLowerCase();
                
                System.out.println("✅ Security question set (temporarily - will reset on app restart)");
                System.out.println("⏎ Press Enter to continue with password reset...");
                InputUtil.getString();
                
                // Now retry with the new security question
                System.out.println("\n🔐 Security Question: " + securityQuestion);
                System.out.print("Your answer: ");
                String userAnswer = InputUtil.getString().toLowerCase();
                
                if (!userAnswer.equals(securityAnswer)) {
                    System.out.println("❌ Incorrect answer!");
                    System.out.println("⏎ Press Enter to continue...");
                    InputUtil.getString();
                    login();
                    return;
                }
            } else {
                System.out.println("❌ Cannot reset password without security question.");
                System.out.println("⏎ Press Enter to continue...");
                InputUtil.getString();
                login();
                return;
            }
        } else {
            // Security question exists, ask it
            System.out.println("\n🔐 Security Question: " + securityQuestion);
            System.out.print("Your answer: ");
            String answer = InputUtil.getString().toLowerCase();
            
            if (!answer.equals(securityAnswer.toLowerCase())) {
                System.out.println("❌ Incorrect answer!");
                System.out.println("⚠️ Please try again or contact support.");
                System.out.println("⏎ Press Enter to continue...");
                InputUtil.getString();
                login();
                return;
            }
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
            login();
        } else {
            System.out.println("❌ Failed to reset password!");
            System.out.println("⏎ Press Enter to continue...");
            InputUtil.getString();
            login();
        }
    }

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
            // Store in currentUser object if possible
            try {
                currentUser.setSecurityQuestion(question);
                currentUser.setSecurityAnswer(answer.toLowerCase());
            } catch (Exception e) {
                // Continue with local storage
            }
            
            // Also update in memory variables for compatibility
            securityQuestion = question;
            securityAnswer = answer.toLowerCase();
            
            System.out.println("\n✅ Security question set successfully!");
            System.out.println("💡 Question: " + question);
            System.out.println("🔐 Answer saved securely.");
        }
        
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
                start();
            } else {
                System.out.println("❌ Failed to update password!");
            }
        }
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
    
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

    // ANALYTICS MENU (Kept in MainMenu as it's a smaller feature)
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
            connections.sort((u1, u2) -> {
                int mut1 = connectionService.getMutualConnections(currentUser.getId(), u1.getId()).size();
                int mut2 = connectionService.getMutualConnections(currentUser.getId(), u2.getId()).size();
                return Integer.compare(mut2, mut1); // Descending order
            });
            
            System.out.println("Ranked by mutual connections:");
            System.out.println("-".repeat(40));
            
            for (int i = 0; i < Math.min(10, connections.size()); i++) {
                User conn = connections.get(i);
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

    // KEEP EXISTING PROFILE METHODS
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
        System.out.println("Security Question: " + (securityQuestion.isEmpty() ? "Not set" : "Set"));
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