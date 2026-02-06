package com.revconnectapp.service;

import com.revconnectapp.dao.NotificationDAO;
import com.revconnectapp.model.Notification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private List<Notification> notifications = new ArrayList<>();
    private NotificationDAO notificationDAO = new NotificationDAO();
    
    // ========== NOTIFICATION CREATION METHODS ==========
    
    // Connection notifications
    public void notifyConnectionRequest(int receiverId, String senderUsername) {
        createNotification(receiverId, 
            "Connection request from @" + senderUsername,
            "CONNECTION_REQUEST");
    }
    
    public void notifyConnectionAccepted(int userId, String acceptorUsername) {
        createNotification(userId,
            "@" + acceptorUsername + " accepted your connection request!",
            "CONNECTION_ACCEPTED");
    }
    
    public void notifyConnectionRejected(int userId, String rejectorUsername) {
        createNotification(userId,
            "@" + rejectorUsername + " rejected your connection request",
            "CONNECTION_REJECTED");
    }
    
    public void notifyNewFollower(int userId, String followerUsername) {
        createNotification(userId,
            "@" + followerUsername + " started following you!",
            "FOLLOWER");
    }
    
    // Post interactions
    public void notifyLikedPost(int postOwnerId, String likerUsername) {
        createNotification(postOwnerId,
            "@" + likerUsername + " liked your post",
            "LIKE");
    }
    
    public void notifyNewComment(int postOwnerId, String commenterUsername) {
        createNotification(postOwnerId,
            "@" + commenterUsername + " commented on your post",
            "COMMENT");
    }
    
    public void notifyPostShared(int originalPosterId, String sharerUsername) {
        createNotification(originalPosterId,
            "@" + sharerUsername + " shared your post",
            "SHARE");
    }
    
    public void notifyMentioned(int userId, String mentionerUsername) {
        createNotification(userId,
            "@" + mentionerUsername + " mentioned you in a post",
            "MENTION");
    }
    
    // System notifications
    public void notifyPasswordChanged(int userId) {
        createNotification(userId,
            "Your password was changed successfully",
            "SECURITY");
    }
    
    public void notifySecurityQuestionSet(int userId) {
        createNotification(userId,
            "Security question set up successfully",
            "SECURITY");
    }
    
    public void notifyWelcome(int userId) {
        createNotification(userId,
            "Welcome to RevConnect! Start connecting with people.",
            "WELCOME");
    }
    
    // Generic notification
    public void notifySystemMessage(int userId, String message, String type) {
        createNotification(userId, message, type);
    }
    
    // ========== CORE METHODS ==========
    
    private void createNotification(int userId, String message, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        // Store in memory for quick access
        notification.setId(notifications.size() + 1);
        notifications.add(notification);
        
        // Also save to database
        try {
            notificationDAO.create(notification);
        } catch (Exception e) {
            System.err.println("Warning: Could not save notification to database: " + e.getMessage());
            // Continue with in-memory storage only
        }
        
        System.out.println("🔔 Created notification for user " + userId + ": " + message);
    }
    
    // ========== NOTIFICATION RETRIEVAL METHODS ==========
    
    public List<Notification> getNotifications(int userId) {
        // Try to get from database first
        try {
            List<Notification> dbNotifications = notificationDAO.getAllForUser(userId);
            if (!dbNotifications.isEmpty()) {
                return dbNotifications;
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load notifications from database: " + e.getMessage());
        }
        
        // Fall back to in-memory notifications
        List<Notification> userNotifications = new ArrayList<>();
        for (Notification n : notifications) {
            if (n.getUserId() == userId) {
                userNotifications.add(n);
            }
        }
        
        // Sort by newest first
        userNotifications.sort((n1, n2) -> {
            if (n1.getCreatedAt() != null && n2.getCreatedAt() != null) {
                return n2.getCreatedAt().compareTo(n1.getCreatedAt());
            }
            return 0;
        });
        
        return userNotifications;
    }
    
    public int getUnreadCount(int userId) {
        // Try database first
        try {
            return notificationDAO.getUnreadCount(userId);
        } catch (Exception e) {
            System.err.println("Warning: Could not get unread count from database: " + e.getMessage());
        }
        
        // Fall back to in-memory
        int count = 0;
        for (Notification n : notifications) {
            if (n.getUserId() == userId && !n.isRead()) {
                count++;
            }
        }
        return count;
    }
    
    public List<Notification> getUnreadNotifications(int userId) {
        // Try database first
        try {
            return notificationDAO.getUnreadNotifications(userId);
        } catch (Exception e) {
            System.err.println("Warning: Could not get unread notifications from database: " + e.getMessage());
        }
        
        // Fall back to in-memory
        List<Notification> unread = new ArrayList<>();
        for (Notification n : notifications) {
            if (n.getUserId() == userId && !n.isRead()) {
                unread.add(n);
            }
        }
        return unread;
    }
    
    public List<Notification> getHistory(int userId) {
        // This is an alias for getAllForUser
        return getNotifications(userId);
    }
    
    public int getTotalCount(int userId) {
        List<Notification> userNotifications = getNotifications(userId);
        return userNotifications.size();
    }
    
    // ========== NOTIFICATION UPDATE METHODS ==========
    
    public void markAsRead(int notificationId) {
        // Update in database
        try {
            notificationDAO.markAsRead(notificationId);
        } catch (Exception e) {
            System.err.println("Warning: Could not mark notification as read in database: " + e.getMessage());
        }
        
        // Update in memory
        for (Notification n : notifications) {
            if (n.getId() == notificationId) {
                n.setRead(true);
                System.out.println("🔔 Marked notification " + notificationId + " as read");
                return;
            }
        }
    }
    
    public void markAllRead(int userId) {
        // Update in database
        try {
            notificationDAO.markAllRead(userId);
        } catch (Exception e) {
            System.err.println("Warning: Could not mark all notifications as read in database: " + e.getMessage());
        }
        
        // Update in memory
        int count = 0;
        for (Notification n : notifications) {
            if (n.getUserId() == userId && !n.isRead()) {
                n.setRead(true);
                count++;
            }
        }
        System.out.println("🔔 Marked " + count + " notifications as read for user " + userId);
    }
    
    // Alias for compatibility
    public void markAllAsRead(int userId) {
        markAllRead(userId);
    }
    
    public void deleteNotification(int notificationId) {
        // Delete from database
        try {
            notificationDAO.deleteNotification(notificationId);
        } catch (Exception e) {
            System.err.println("Warning: Could not delete notification from database: " + e.getMessage());
        }
        
        // Delete from memory
        notifications.removeIf(n -> n.getId() == notificationId);
        System.out.println("🔔 Deleted notification " + notificationId);
    }
    
    public void deleteAllReadNotifications(int userId) {
        // Delete from database
        try {
            int deletedCount = notificationDAO.deleteAllRead(userId);
            System.out.println("🔔 Deleted " + deletedCount + " read notifications from database for user " + userId);
        } catch (Exception e) {
            System.err.println("Warning: Could not delete read notifications from database: " + e.getMessage());
        }
        
        // Delete from memory
        int initialSize = notifications.size();
        notifications.removeIf(n -> n.getUserId() == userId && n.isRead());
        int deletedCount = initialSize - notifications.size();
        System.out.println("🔔 Deleted " + deletedCount + " read notifications for user " + userId);
    }
    
    public void clearAllNotifications(int userId) {
        // Clear from database (would need a new method in DAO)
        // For now, just clear from memory
        int initialSize = notifications.size();
        notifications.removeIf(n -> n.getUserId() == userId);
        int deletedCount = initialSize - notifications.size();
        System.out.println("🔔 Cleared " + deletedCount + " notifications for user " + userId);
    }
    
    // ========== HELPER METHODS ==========
    
    public List<Notification> getNotificationsByType(int userId, String type) {
        // Try database first
        try {
            return notificationDAO.getByType(userId, type);
        } catch (Exception e) {
            System.err.println("Warning: Could not get notifications by type from database: " + e.getMessage());
        }
        
        // Fall back to in-memory
        List<Notification> filtered = new ArrayList<>();
        for (Notification n : notifications) {
            if (n.getUserId() == userId && type.equals(n.getType())) {
                filtered.add(n);
            }
        }
        return filtered;
    }
    
    public List<Notification> getLatest(int userId, int limit) {
        // Try database first
        try {
            return notificationDAO.getLatest(userId, limit);
        } catch (Exception e) {
            System.err.println("Warning: Could not get latest notifications from database: " + e.getMessage());
        }
        
        // Fall back to in-memory
        List<Notification> all = getNotifications(userId);
        if (all.size() <= limit) {
            return all;
        }
        return all.subList(0, Math.min(limit, all.size()));
    }
}