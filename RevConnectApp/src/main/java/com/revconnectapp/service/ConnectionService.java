package com.revconnectapp.service;

import com.revconnectapp.dao.ConnectionDAO;
import com.revconnectapp.model.Connection;
import com.revconnectapp.model.User;
import java.util.List;

public class ConnectionService {
    private ConnectionDAO connectionDAO = new ConnectionDAO();
    private UserService userService = new UserService();
    private NotificationService notificationService = new NotificationService();
    
    // REMOVE these problematic methods (lines 155-173):
    // public User findUserByUsername(String username) {
    //     // Use UserService if available, or search directly
    //     for (User user : database.getUsers()) {  // ERROR: database not declared
    //         if (user.getUsername().equalsIgnoreCase(username)) {
    //             return user;
    //         }
    //     }
    //     return null;
    // }
    
    // public User getUserByUsernameAlternative(String username) {
    //     // Try to find user in the database
    //     for (User user : database.getUsers()) {  // ERROR: database not declared
    //         if (user.getUsername().equalsIgnoreCase(username)) {
    //             return user;
    //         }
    //     }
    //     return null;
    // }
    
    // ✅ IMPROVED: Send connection request with better error handling
    public boolean sendRequest(int fromUserId, int toUserId) {
        System.out.println("🔍 DEBUG: Sending connection request from user " + fromUserId + " to user " + toUserId);
        
        if (fromUserId == toUserId) {
            System.out.println("❌ You cannot send connection request to yourself!");
            return false;
        }
        
        User fromUser = userService.getUserById(fromUserId);
        User toUser = userService.getUserById(toUserId);
        
        if (fromUser == null || toUser == null) {
            System.out.println("❌ One or both users not found!");
            return false;
        }
        
        System.out.println("🔍 DEBUG: From User: " + fromUser.getUsername() + " (Type: " + fromUser.getUserType() + ")");
        System.out.println("🔍 DEBUG: To User: " + toUser.getUsername() + " (Type: " + toUser.getUserType() + ")");
        
        // Check if both are personal users
        if (!fromUser.getUserType().equals("PERSONAL") || !toUser.getUserType().equals("PERSONAL")) {
            System.out.println("❌ You can only send connection requests to other personal users!");
            System.out.println("   For creators/business accounts, use the Follow feature.");
            return false;
        }
        
        // Check if connection already exists
        if (connectionDAO.connectionExists(fromUserId, toUserId)) {
            String status = getConnectionStatus(fromUserId, toUserId);
            System.out.println("⚠️ Connection already exists! Status: " + status);
            return false;
        }
        
        boolean success = connectionDAO.createRequest(fromUserId, toUserId);
        if (success) {
            System.out.println("✅ Connection request sent to " + toUser.getUsername() + "!");
            notificationService.notifyConnectionRequest(toUserId, fromUser.getUsername());
        } else {
            System.out.println("❌ Failed to send connection request!");
        }
        return success;
    }
    
    // ✅ NEW: Send request with username instead of ID
    public boolean sendRequestByUsername(int fromUserId, String toUsername) {
        User toUser = userService.getUserByUsername(toUsername);
        if (toUser == null) {
            System.out.println("❌ User @" + toUsername + " not found!");
            return false;
        }
        return sendRequest(fromUserId, toUser.getId());
    }
    
    // ✅ IMPROVED: Get pending requests with user details
    public List<Connection> getPendingRequests(int userId) {
        List<Connection> requests = connectionDAO.getPendingRequests(userId);
        System.out.println("🔍 DEBUG: Found " + requests.size() + " pending requests for user " + userId);
        return requests;
    }
    
    // ✅ IMPROVED: Accept connection request by connection ID
    public boolean acceptRequest(int connectionId) {
        System.out.println("🔍 DEBUG: Accepting connection with ID: " + connectionId);
        
        Connection connection = getConnectionById(connectionId);
        if (connection == null) {
            System.out.println("❌ Connection not found!");
            return false;
        }
        
        if (!connection.getStatus().equals("PENDING")) {
            System.out.println("❌ This connection is not pending! Current status: " + connection.getStatus());
            return false;
        }
        
        boolean success = connectionDAO.updateStatus(connectionId, "ACCEPTED");
        if (success) {
            User receiver = userService.getUserById(connection.getUser2Id());
            User sender = userService.getUserById(connection.getUser1Id());
            
            if (receiver != null && sender != null) {
                System.out.println("✅ Connection accepted! " + receiver.getUsername() + " and " + 
                                 sender.getUsername() + " are now connected.");
                notificationService.notifyConnectionAccepted(sender.getId(), receiver.getUsername());
            }
        } else {
            System.out.println("❌ Failed to accept connection!");
        }
        return success;
    }
    
    // ✅ IMPROVED: Accept connection request by user IDs (with better validation)
    public boolean acceptRequest(int receiverId, int senderId) {
        System.out.println("🔍 DEBUG: Accepting request - Receiver: " + receiverId + ", Sender: " + senderId);
        
        if (receiverId == senderId) {
            System.out.println("❌ You cannot accept a request from yourself!");
            return false;
        }
        
        // Check if sender exists
        User sender = userService.getUserById(senderId);
        if (sender == null) {
            System.out.println("❌ Sender user not found!");
            return false;
        }
        
        // Find the connection
        Connection connection = getPendingConnectionBetweenUsers(senderId, receiverId);
        if (connection == null) {
            System.out.println("❌ No PENDING connection request found from user " + sender.getUsername() + "!");
            
            // Check if connection exists in other status
            Connection existingConn = getConnectionBetweenUsers(senderId, receiverId);
            if (existingConn != null) {
                System.out.println("ℹ️ Connection exists with status: " + existingConn.getStatus());
            }
            return false;
        }
        
        boolean success = connectionDAO.updateStatus(connection.getId(), "ACCEPTED");
        if (success) {
            User receiver = userService.getUserById(receiverId);
            
            if (receiver != null) {
                System.out.println("✅ Connection accepted! You are now connected with " + sender.getUsername());
                notificationService.notifyConnectionAccepted(senderId, receiver.getUsername());
            }
        } else {
            System.out.println("❌ Failed to accept connection!");
        }
        return success;
    }
    
    // ✅ NEW: Accept request by username
    public boolean acceptRequestByUsername(int receiverId, String senderUsername) {
        User sender = userService.getUserByUsername(senderUsername);
        if (sender == null) {
            System.out.println("❌ User @" + senderUsername + " not found!");
            return false;
        }
        return acceptRequest(receiverId, sender.getId());
    }
    
    // ✅ NEW: Reject request with notification
    public boolean rejectRequest(int receiverId, int senderId) {
        Connection connection = getPendingConnectionBetweenUsers(senderId, receiverId);
        if (connection == null) {
            System.out.println("❌ No pending request to reject!");
            return false;
        }
        
        boolean success = connectionDAO.updateStatus(connection.getId(), "REJECTED");
        if (success) {
            User sender = userService.getUserById(senderId);
            System.out.println("✅ Connection request from " + (sender != null ? sender.getUsername() : "user") + " rejected.");
        }
        return success;
    }
    
    // ✅ IMPROVED: Get all connections with mutual connections count
    public List<User> getConnections(int userId) {
        List<User> connections = connectionDAO.getConnections(userId);
        System.out.println("🔍 DEBUG: User " + userId + " has " + connections.size() + " connections");
        return connections;
    }
    
    // ✅ NEW: Get mutual connections between two users
    public List<User> getMutualConnections(int userId1, int userId2) {
        return connectionDAO.getMutualConnections(userId1, userId2);
    }
    
    // ✅ NEW: Get connection suggestions (friends of friends)
    public List<User> getConnectionSuggestions(int userId) {
        return connectionDAO.getConnectionSuggestions(userId);
    }
    
    // ✅ IMPROVED: Remove connection with confirmation
    public boolean removeConnection(int userId1, int userId2) {
        if (!areConnected(userId1, userId2)) {
            System.out.println("❌ You are not connected with this user!");
            return false;
        }
        
        boolean success = connectionDAO.removeConnection(userId1, userId2);
        if (success) {
            User otherUser = userService.getUserById(userId2);
            System.out.println("✅ Connection with " + (otherUser != null ? otherUser.getUsername() : "user") + " removed.");
        }
        return success;
    }
    
    // ✅ NEW: Remove connection by username
    public boolean removeConnectionByUsername(int userId, String username) {
        User otherUser = userService.getUserByUsername(username);
        if (otherUser == null) {
            System.out.println("❌ User @" + username + " not found!");
            return false;
        }
        return removeConnection(userId, otherUser.getId());
    }
    
    // ✅ NEW: Get pending connection between users (only PENDING status)
    public Connection getPendingConnectionBetweenUsers(int userId1, int userId2) {
        String sql = "SELECT * FROM connections WHERE " +
                    "((user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)) " +
                    "AND status = 'PENDING'";
        
        return connectionDAO.getConnectionByQuery(sql, userId1, userId2, userId2, userId1);
    }
    
    // ✅ NEW: Check if request was sent (regardless of status)
    public boolean requestWasSent(int fromUserId, int toUserId) {
        return connectionDAO.connectionExists(fromUserId, toUserId);
    }
    
    // ✅ NEW: Get connection statistics
    public void printConnectionStats(int userId) {
        int totalConnections = getConnectionCount(userId);
        int pendingReceived = getPendingRequestsCount(userId);
        int pendingSent = getSentRequestsCount(userId);
        
        System.out.println("\n📊 CONNECTION STATISTICS:");
        System.out.println("Total Connections: " + totalConnections);
        System.out.println("Pending Requests Received: " + pendingReceived);
        System.out.println("Pending Requests Sent: " + pendingSent);
        
        if (pendingReceived > 0) {
            System.out.println("\n📥 Pending requests waiting for your response!");
        }
    }
    
    // ✅ NEW: Get sent requests count
    public int getSentRequestsCount(int userId) {
        List<Connection> sent = getSentRequests(userId);
        return sent.size();
    }
    
    // ✅ NEW: Cancel a sent request
    public boolean cancelSentRequest(int fromUserId, int toUserId) {
        Connection connection = getPendingConnectionBetweenUsers(fromUserId, toUserId);
        if (connection == null) {
            System.out.println("❌ No pending request found to cancel!");
            return false;
        }
        
        if (connection.getUser1Id() != fromUserId) {
            System.out.println("❌ You can only cancel requests that you sent!");
            return false;
        }
        
        return connectionDAO.deleteConnection(connection.getId());
    }
    
    // ✅ NEW: Get connection strength (mutual connections count)
    public int getConnectionStrength(int userId1, int userId2) {
        List<User> mutuals = getMutualConnections(userId1, userId2);
        return mutuals.size();
    }
    
    // ✅ NEW: Are users connected or pending?
    public String getRelationshipStatus(int userId1, int userId2) {
        Connection conn = getConnectionBetweenUsers(userId1, userId2);
        if (conn == null) return "NO_RELATIONSHIP";
        
        if (conn.getStatus().equals("PENDING")) {
            if (conn.getUser1Id() == userId1) {
                return "REQUEST_SENT";
            } else {
                return "REQUEST_RECEIVED";
            }
        }
        return conn.getStatus();
    }
    
    // ✅ Keep existing methods with minor improvements:
    
    public boolean areConnected(int userId1, int userId2) {
        return connectionDAO.areConnected(userId1, userId2);
    }
    
    public Connection getConnectionById(int connectionId) {
        return connectionDAO.getConnectionById(connectionId);
    }
    
    public Connection getConnectionBetweenUsers(int userId1, int userId2) {
        return connectionDAO.getConnectionBetweenUsers(userId1, userId2);
    }
    
    public List<Connection> getSentRequests(int userId) {
        return connectionDAO.getSentRequests(userId);
    }
    
    public boolean cancelRequest(int connectionId) {
        return connectionDAO.deleteConnection(connectionId);
    }
    
    public int getConnectionCount(int userId) {
        return connectionDAO.getConnectionCount(userId);
    }
    
    public int getPendingRequestsCount(int userId) {
        return connectionDAO.getPendingRequestsCount(userId);
    }
    
    public boolean canConnect(int userId1, int userId2) {
        if (userId1 == userId2) return false;
        
        User user1 = userService.getUserById(userId1);
        User user2 = userService.getUserById(userId2);
        
        if (user1 == null || user2 == null) return false;
        
        return user1.getUserType().equals("PERSONAL") && user2.getUserType().equals("PERSONAL");
    }
    
    public String getConnectionStatus(int userId1, int userId2) {
        Connection connection = getConnectionBetweenUsers(userId1, userId2);
        if (connection == null) {
            return "NO_CONNECTION";
        }
        return connection.getStatus();
    }
    
    // ✅ NEW: For compatibility with old MainMenu
    public void sendRequestCompatible(int senderId, int receiverId) {
        sendRequest(senderId, receiverId);
    }
}