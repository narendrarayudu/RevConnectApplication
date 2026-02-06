package com.revconnectapp.ui;

import com.revconnectapp.model.User;
import com.revconnectapp.model.Connection;
import com.revconnectapp.service.UserService;
import com.revconnectapp.service.ConnectionService;
import com.revconnectapp.util.InputUtil;
import java.util.List;


public class ConnectionsMenu {
    private User currentUser;
    private UserService userService;
    private ConnectionService connectionService;
    
    public ConnectionsMenu(User currentUser, UserService userService, ConnectionService connectionService) {
        this.currentUser = currentUser;
        this.userService = userService;
        this.connectionService = connectionService;
    }
    
    public void show() {
        while (true) {
            System.out.println("\n👥 === CONNECTIONS ===");
            System.out.println("1. 🔍 Find Users");
            System.out.println("2. 📥 Incoming Requests");
            System.out.println("3. ✅ Accept Requests (by username)");
            System.out.println("4. 👥 My Connections");
            System.out.println("0. ↩️ Back to Dashboard");
            
            System.out.print("\nYour choice: ");
            int choice = InputUtil.getInt();
            
            switch (choice) {
                case 1 -> findAndConnectUsers();
                case 2 -> showIncomingRequests();
                case 3 -> acceptConnectionRequest();
                case 4 -> showMyConnections();
                case 0 -> { return; }
                default -> System.out.println("❌ Invalid choice!");
            }
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
                    } else {
                        System.out.println("❌ Failed to send connection request!");
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
            } else {
                System.out.println("❌ Failed to accept request!");
            }
        } else {
            System.out.println("❌ Invalid selection!");
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
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
                } else {
                    System.out.println("❌ Failed to reject request!");
                }
            } else {
                System.out.println("❌ Rejection cancelled.");
            }
        } else {
            System.out.println("❌ Invalid selection!");
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

    private void acceptConnectionRequest() {
        System.out.println("\n✅ ACCEPT REQUESTS BY USERNAME");
        System.out.print("Enter sender's username: ");
        String senderUsername = InputUtil.getString();
        
        boolean success = connectionService.acceptRequestByUsername(currentUser.getId(), senderUsername);
        if (success) {
            System.out.println("✅ Connection accepted!");
        } else {
            System.out.println("❌ Failed to accept connection request. User not found or no pending request.");
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }

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

    private void showConnectionStats() {
        int totalConnections = connectionService.getConnectionCount(currentUser.getId());
        int pendingReceived = connectionService.getPendingRequestsCount(currentUser.getId());
        int pendingSent = connectionService.getSentRequestsCount(currentUser.getId());
        
        System.out.println("\n📊 CONNECTION STATISTICS:");
        System.out.println("✅ Current Connections: " + totalConnections);
        System.out.println("📥 Pending Requests (incoming): " + pendingReceived);
        System.out.println("📤 Pending Requests (outgoing): " + pendingSent);
        System.out.println("📊 Total Network Size: " + (totalConnections + pendingReceived + pendingSent));
        
        // Calculate network health
        if (totalConnections + pendingReceived + pendingSent > 0) {
            double healthScore = (double) totalConnections / (totalConnections + pendingReceived + pendingSent) * 100;
            System.out.printf("🏆 Network Health Score: %.1f%%\n", healthScore);
            
            if (healthScore > 80) {
                System.out.println("💪 Excellent network health!");
            } else if (healthScore > 60) {
                System.out.println("👍 Good network, keep growing!");
            } else {
                System.out.println("💡 Try to convert more pending requests to connections!");
            }
        }
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
}