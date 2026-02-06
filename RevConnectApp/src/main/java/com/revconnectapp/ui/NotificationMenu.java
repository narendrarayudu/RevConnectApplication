package com.revconnectapp.ui;

import com.revconnectapp.model.User;
import com.revconnectapp.model.Notification;
import com.revconnectapp.service.NotificationService;
import com.revconnectapp.util.InputUtil;
import java.util.List;

public class NotificationMenu {
    private NotificationService notificationService = new NotificationService();
    
    public void show(User currentUser) {
        System.out.println("\n🔔 === NOTIFICATIONS ===");
        System.out.println("=".repeat(50));
        
        int unreadCount = notificationService.getUnreadCount(currentUser.getId());
        int totalCount = notificationService.getTotalCount(currentUser.getId());
        
        System.out.println("📊 Stats: " + unreadCount + " unread / " + totalCount + " total");
        System.out.println("-".repeat(50));
        
        List<Notification> notifications = notificationService.getNotifications(currentUser.getId());
        
        if (notifications.isEmpty()) {
            System.out.println("📭 No notifications yet!");
            System.out.println("💡 Connect with people and engage with posts to get notifications.");
        } else {
            // Display notifications
            for (int i = 0; i < notifications.size(); i++) {
                Notification n = notifications.get(i);
                String statusIcon = n.isRead() ? "📭" : "📬";
                String statusText = n.isRead() ? "READ" : "NEW";
                
                System.out.println("\n" + (i + 1) + ". " + statusIcon + " [" + statusText + "]");
                System.out.println("   📝 " + n.getMessage());
                System.out.println("   ⏰ " + n.getCreatedAt());
                System.out.println("   🆔 ID: " + n.getId());
                System.out.println("   " + "-".repeat(40));
            }
            
            // Action menu
            System.out.println("\n🎯 NOTIFICATION ACTIONS:");
            System.out.println("1. 📖 Mark as Read (by number)");
            System.out.println("2. 📖 Mark All as Read");
            System.out.println("3. 🗑️ Delete Notification (by number)");
            System.out.println("4. 🗑️ Delete All Read Notifications");
            System.out.println("5. 🔄 Refresh List");
            System.out.println("0. ↩️ Back to Dashboard");
            
            System.out.print("\nYour choice: ");
            int choice = InputUtil.getInt();
            
            switch (choice) {
                case 1 -> markAsRead(notifications, currentUser.getId());
                case 2 -> markAllAsRead(currentUser.getId());
                case 3 -> deleteNotification(notifications, currentUser.getId());
                case 4 -> deleteAllReadNotifications(currentUser.getId());
                case 5 -> show(currentUser); // Refresh
                default -> {}
            }
        }
        
        System.out.println("\n⏎ Press Enter to continue...");
        InputUtil.getString();
    }
    
    private void markAsRead(List<Notification> notifications, int userId) {
        if (notifications.isEmpty()) return;
        
        System.out.print("Enter notification number to mark as read: ");
        int index = InputUtil.getInt() - 1;
        
        if (index >= 0 && index < notifications.size()) {
            Notification notification = notifications.get(index);
            if (!notification.isRead()) {
                notificationService.markAsRead(notification.getId());
                System.out.println("✅ Notification marked as read!");
            } else {
                System.out.println("ℹ️ Notification is already read.");
            }
        } else {
            System.out.println("❌ Invalid selection!");
        }
    }
    
    private void markAllAsRead(int userId) {
        System.out.print("Mark ALL notifications as read? (y/n): ");
        if (InputUtil.getString().equalsIgnoreCase("y")) {
            notificationService.markAllAsRead(userId);
            System.out.println("✅ All notifications marked as read!");
        } else {
            System.out.println("❌ Cancelled.");
        }
    }
    
    private void deleteNotification(List<Notification> notifications, int userId) {
        if (notifications.isEmpty()) return;
        
        System.out.print("Enter notification number to delete: ");
        int index = InputUtil.getInt() - 1;
        
        if (index >= 0 && index < notifications.size()) {
            Notification notification = notifications.get(index);
            System.out.print("Delete notification: \"" + 
                (notification.getMessage().length() > 50 ? 
                 notification.getMessage().substring(0, 50) + "..." : 
                 notification.getMessage()) + 
                "\"? (y/n): ");
            
            if (InputUtil.getString().equalsIgnoreCase("y")) {
                notificationService.deleteNotification(notification.getId());
                System.out.println("✅ Notification deleted!");
            } else {
                System.out.println("❌ Deletion cancelled.");
            }
        } else {
            System.out.println("❌ Invalid selection!");
        }
    }
    
    private void deleteAllReadNotifications(int userId) {
        System.out.print("Delete ALL read notifications? (y/n): ");
        if (InputUtil.getString().equalsIgnoreCase("y")) {
            notificationService.deleteAllReadNotifications(userId);
            System.out.println("✅ All read notifications deleted!");
        } else {
            System.out.println("❌ Cancelled.");
        }
    }
}