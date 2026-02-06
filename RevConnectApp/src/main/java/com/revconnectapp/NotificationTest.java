package com.revconnectapp;

import com.revconnectapp.service.NotificationService;
import com.revconnectapp.model.Notification;
import java.util.List;

public class NotificationTest {
    public static void main(String[] args) {
        NotificationService service = new NotificationService();
        
        System.out.println("🧪 TESTING NOTIFICATION SYSTEM");
        System.out.println("==============================");
        
        // Test 1: Create a notification
        System.out.println("\nTest 1: Creating connection request notification...");
        service.notifyConnectionRequest(1, "testuser");
        
        // Test 2: Create more notifications
        System.out.println("Creating like notification...");
        service.notifyLikedPost(1, "john_doe");
        
        System.out.println("Creating comment notification...");
        service.notifyNewComment(1, "jane_smith");
        
        // Test 3: Check unread count
        System.out.println("\nTest 2: Checking unread count...");
        int count = service.getUnreadCount(1);
        System.out.println("Unread notifications: " + count);
        
        // Test 4: Get history
        System.out.println("\nTest 3: Getting notification history...");
        List<Notification> notifications = service.getHistory(1);
        System.out.println("Total notifications: " + notifications.size());
        
        // Display notifications
        for (int i = 0; i < Math.min(5, notifications.size()); i++) {
            Notification n = notifications.get(i);
            System.out.printf("%d. %s %s - %s%n", 
                (i+1), n.getTypeEmoji(), n.getMessage(), 
                n.isRead() ? "📭 Read" : "📬 Unread");
        }
        
        // Test 5: Mark as read
        System.out.println("\nTest 4: Marking all as read...");
        service.markAllRead(1);
        
        // Verify
        int newCount = service.getUnreadCount(1);
        System.out.println("Unread count after marking read: " + newCount);
        
        System.out.println("\n✅ Notification test complete!");
    }
}