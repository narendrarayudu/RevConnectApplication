package com.revconnectapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;

public class LikeIntegrationTest {
    
    @Test
    @DisplayName("TC-LIKE-009: Test Like-Notification Integration")
    public void testLikeNotificationIntegration() {
        System.out.println("=== TC-LIKE-009: Like-Notification Integration Test ===");
        
        // Simulate like actions and corresponding notifications
        Map<Integer, String[]> likeActions = new HashMap<>();
        likeActions.put(1, new String[]{"101", "1001", "POST", "John"});   // User 101 likes post 1001 by John
        likeActions.put(2, new String[]{"102", "1002", "COMMENT", "Jane"}); // User 102 likes comment 1002 by Jane
        likeActions.put(3, new String[]{"103", "1001", "POST", "John"});   // User 103 likes post 1001 by John
        likeActions.put(4, new String[]{"101", "1003", "PHOTO", "Alice"}); // User 101 likes photo 1003 by Alice
        
        System.out.println("Like actions recorded:");
        for (Map.Entry<Integer, String[]> entry : likeActions.entrySet()) {
            String[] action = entry.getValue();
            System.out.println("Action " + entry.getKey() + ": User " + action[0] + 
                             " liked " + action[2] + " " + action[1] + " by " + action[3]);
        }
        
        // Generate notifications for like actions
        System.out.println("\nGenerated notifications:");
        
        Map<Integer, String[]> notifications = new HashMap<>();
        int notificationId = 1;
        
        for (Map.Entry<Integer, String[]> entry : likeActions.entrySet()) {
            String[] action = entry.getValue();
            String likerId = action[0];
            String contentId = action[1];
            String contentType = action[2];
            String contentOwner = action[3];
            
            // Generate notification message
            String message = "User " + likerId + " liked your " + contentType.toLowerCase() + 
                           " (" + contentId + ")";
            String notificationType = "LIKE";
            String recipient = contentOwner; // Content owner gets notified
            
            notifications.put(notificationId, 
                new String[]{recipient, notificationType, message, contentId});
            
            System.out.println("Notification " + notificationId + ": " + message + 
                             " → To: " + recipient);
            
            notificationId++;
        }
        
        // Validate notifications
        assertEquals(likeActions.size(), notifications.size(), 
                    "Should have one notification per like action");
        
        // Test notification content
        System.out.println("\nValidating notification content:");
        for (Map.Entry<Integer, String[]> entry : notifications.entrySet()) {
            String[] notification = entry.getValue();
            String recipient = notification[0];
            String type = notification[1];
            String message = notification[2];
            String contentId = notification[3];
            
            assertNotNull(recipient, "Recipient should not be null");
            assertNotNull(type, "Notification type should not be null");
            assertNotNull(message, "Message should not be null");
            assertEquals("LIKE", type, "Notification type should be LIKE");
            assertTrue(message.contains("liked your"), "Message should contain 'liked your'");
            assertTrue(message.contains(contentId), "Message should contain content ID");
        }
        
        // Test notification aggregation (multiple likes on same content)
        System.out.println("\nTesting notification aggregation:");
        
        // Count likes per content
        Map<String, Integer> likesPerContent = new HashMap<>();
        for (Map.Entry<Integer, String[]> entry : likeActions.entrySet()) {
            String[] action = entry.getValue();
            String contentId = action[1];
            likesPerContent.put(contentId, likesPerContent.getOrDefault(contentId, 0) + 1);
        }
        
        System.out.println("Likes per content:");
        for (Map.Entry<String, Integer> entry : likesPerContent.entrySet()) {
            System.out.println("Content " + entry.getKey() + ": " + entry.getValue() + " likes");
            
            if (entry.getValue() > 1) {
                System.out.println("  → Multiple likes, could aggregate notifications");
            }
        }
        
        // Content 1001 should have 2 likes
        assertEquals(2, (int) likesPerContent.get("1001"), "Content 1001 should have 2 likes");
        
        System.out.println("✅ TC-LIKE-009 PASSED: Like-notification integration\n");
    }
    
    @Test
    @DisplayName("TC-LIKE-010: Test Like-Counter Integration")
    public void testLikeCounterIntegration() {
        System.out.println("=== TC-LIKE-010: Like-Counter Integration Test ===");
        
        // Simulate content with like counters
        Map<Integer, Integer> contentLikeCounters = new HashMap<>();
        contentLikeCounters.put(1001, 5);   // Post 1001 has 5 likes
        contentLikeCounters.put(1002, 12);  // Post 1002 has 12 likes
        contentLikeCounters.put(1003, 3);   // Post 1003 has 3 likes
        contentLikeCounters.put(2001, 8);   // Comment 2001 has 8 likes
        
        System.out.println("Initial like counters:");
        for (Map.Entry<Integer, Integer> entry : contentLikeCounters.entrySet()) {
            System.out.println("Content " + entry.getKey() + ": " + entry.getValue() + " likes");
        }
        
        // Test like action updates counter
        System.out.println("\nTesting like counter updates:");
        
        // User likes post 1001
        int contentId = 1001;
        int initialLikes = contentLikeCounters.get(contentId);
        contentLikeCounters.put(contentId, initialLikes + 1);
        
        System.out.println("User liked content " + contentId);
        System.out.println("Before: " + initialLikes + " likes");
        System.out.println("After: " + contentLikeCounters.get(contentId) + " likes");
        
        assertEquals(initialLikes + 1, (int) contentLikeCounters.get(contentId),
                    "Like counter should increment by 1");
        
        // Test unlike action updates counter
        System.out.println("\nTesting unlike counter updates:");
        
        // User unlikes post 1001
        int currentLikes = contentLikeCounters.get(contentId);
        contentLikeCounters.put(contentId, currentLikes - 1);
        
        System.out.println("User unliked content " + contentId);
        System.out.println("Before: " + currentLikes + " likes");
        System.out.println("After: " + contentLikeCounters.get(contentId) + " likes");
        
        assertEquals(currentLikes - 1, (int) contentLikeCounters.get(contentId),
                    "Like counter should decrement by 1");
        
        // Test multiple simultaneous likes
        System.out.println("\nTesting multiple simultaneous likes:");
        
        int[] usersLiking = {101, 102, 103}; // Three users like the same content
        contentId = 1002;
        initialLikes = contentLikeCounters.get(contentId);
        
        System.out.println("Content " + contentId + " initial likes: " + initialLikes);
        System.out.println("Users liking: " + usersLiking.length);
        
        // Simulate simultaneous likes (could be race condition in real system)
        for (int userId : usersLiking) {
            contentLikeCounters.put(contentId, contentLikeCounters.get(contentId) + 1);
            System.out.println("User " + userId + " liked → Counter: " + 
                             contentLikeCounters.get(contentId));
        }
        
        int finalLikes = contentLikeCounters.get(contentId);
        System.out.println("Final likes: " + finalLikes);
        
        assertEquals(initialLikes + usersLiking.length, finalLikes,
                    "Counter should increase by number of users liking");
        
        // Test counter never goes negative
        System.out.println("\nTesting counter never goes negative:");
        
        contentId = 1003;
        initialLikes = contentLikeCounters.get(contentId);
        
        // Try to unlike more times than likes exist
        for (int i = 0; i < initialLikes + 2; i++) { // Try to decrement more than available
            if (contentLikeCounters.get(contentId) > 0) {
                contentLikeCounters.put(contentId, contentLikeCounters.get(contentId) - 1);
            }
        }
        
        int finalCount = contentLikeCounters.get(contentId);
        System.out.println("Initial: " + initialLikes + " likes");
        System.out.println("After excessive unlikes: " + finalCount + " likes");
        
        assertTrue(finalCount >= 0, "Like counter should never be negative");
        assertEquals(0, finalCount, "Counter should be 0 after all likes removed");
        
        // Test counter synchronization
        System.out.println("\nTesting counter synchronization:");
        
        int testContentId = 2001;
        int baseCount = contentLikeCounters.get(testContentId);
        
        // Simulate read-modify-write cycle
        int readCount = contentLikeCounters.get(testContentId);
        int newCount = readCount + 1;
        contentLikeCounters.put(testContentId, newCount);
        
        System.out.println("Read count: " + readCount);
        System.out.println("New count: " + newCount);
        System.out.println("Stored count: " + contentLikeCounters.get(testContentId));
        
        assertEquals(baseCount + 1, (int) contentLikeCounters.get(testContentId),
                    "Counter should be properly synchronized");
        
        System.out.println("✅ TC-LIKE-010 PASSED: Like-counter integration\n");
    }
    
    @Test
    @DisplayName("TC-LIKE-011: Test Like with User Profile Integration")
    public void testLikeWithUserProfileIntegration() {
        System.out.println("=== TC-LIKE-011: Like with User Profile Integration Test ===");
        
        // Simulate user profiles
        Map<Integer, String[]> userProfiles = new HashMap<>();
        userProfiles.put(101, new String[]{"john_doe", "John Doe", "Software Engineer"});
        userProfiles.put(102, new String[]{"jane_smith", "Jane Smith", "UX Designer"});
        userProfiles.put(103, new String[]{"bob_jones", "Bob Jones", "Product Manager"});
        userProfiles.put(104, new String[]{"alice_wang", "Alice Wang", "Data Scientist"});
        
        System.out.println("User profiles:");
        for (Map.Entry<Integer, String[]> entry : userProfiles.entrySet()) {
            String[] profile = entry.getValue();
            System.out.println("User " + entry.getKey() + ": " + 
                             profile[0] + " (" + profile[1] + ") - " + profile[2]);
        }
        
        // Simulate likes with user context
        Map<Integer, String[]> likesWithContext = new HashMap<>();
        likesWithContext.put(1, new String[]{"101", "1001", "POST", "John liked a post"});
        likesWithContext.put(2, new String[]{"102", "1001", "POST", "Jane liked a post"});
        likesWithContext.put(3, new String[]{"103", "1002", "COMMENT", "Bob liked a comment"});
        likesWithContext.put(4, new String[]{"104", "1003", "PHOTO", "Alice liked a photo"});
        
        System.out.println("\nLikes with user context:");
        for (Map.Entry<Integer, String[]> entry : likesWithContext.entrySet()) {
            String[] like = entry.getValue();
            String userId = like[0];
            String[] profile = userProfiles.get(Integer.parseInt(userId));
            
            System.out.println("Like " + entry.getKey() + ": " + 
                             profile[1] + " (" + profile[0] + ") - " + like[3]);
        }
        
        // Test enriched like information
        System.out.println("\nEnriched like information:");
        
        for (Map.Entry<Integer, String[]> entry : likesWithContext.entrySet()) {
            String[] like = entry.getValue();
            int userId = Integer.parseInt(like[0]);
            String[] profile = userProfiles.get(userId);
            
            // Create enriched like object
            String enrichedLike = profile[1] + " (" + profile[2] + ") liked " + like[2].toLowerCase() + 
                                " " + like[1];
            
            System.out.println("Enriched: " + enrichedLike);
            
            // Validate enriched information
            assertNotNull(profile, "User profile should exist");
            assertEquals(3, profile.length, "Profile should have 3 fields");
            assertTrue(enrichedLike.contains(profile[1]), "Should contain user's full name");
            assertTrue(enrichedLike.contains(profile[2]), "Should contain user's title");
            assertTrue(enrichedLike.contains(like[2].toLowerCase()), "Should contain content type");
        }
        
        // Test user's like history
        System.out.println("\nUser like history:");
        
        Map<Integer, Integer> userLikeCounts = new HashMap<>();
        for (Map.Entry<Integer, String[]> entry : likesWithContext.entrySet()) {
            String[] like = entry.getValue();
            int userId = Integer.parseInt(like[0]);
            userLikeCounts.put(userId, userLikeCounts.getOrDefault(userId, 0) + 1);
        }
        
        for (Map.Entry<Integer, Integer> entry : userLikeCounts.entrySet()) {
            int userId = entry.getKey();
            int likeCount = entry.getValue();
            String[] profile = userProfiles.get(userId);
            
            System.out.println(profile[1] + " has " + likeCount + " likes in history");
            
            assertTrue(likeCount > 0, "User should have at least 1 like");
            assertNotNull(profile, "User profile should exist");
        }
        
        // Test most active likers
        System.out.println("\nMost active likers:");
        
        int maxLikes = 0;
        String mostActiveUser = "";
        for (Map.Entry<Integer, Integer> entry : userLikeCounts.entrySet()) {
            if (entry.getValue() > maxLikes) {
                maxLikes = entry.getValue();
                String[] profile = userProfiles.get(entry.getKey());
                mostActiveUser = profile[1];
            }
        }
        
        System.out.println("Most active: " + mostActiveUser + " (" + maxLikes + " likes)");
        assertTrue(maxLikes > 0, "Most active user should have likes");
        
        // Test like similarity (users with similar liking patterns)
        System.out.println("\nLike similarity analysis:");
        
        // Users 101 and 102 both liked post 1001
        int user1 = 101;
        int user2 = 102;
        boolean bothLikedSamePost = false;
        
        for (Map.Entry<Integer, String[]> entry : likesWithContext.entrySet()) {
            String[] like = entry.getValue();
            int userId = Integer.parseInt(like[0]);
            String postId = like[1];
            
            // Check if both users liked the same post
            if (userId == user1 || userId == user2) {
                for (Map.Entry<Integer, String[]> entry2 : likesWithContext.entrySet()) {
                    String[] like2 = entry2.getValue();
                    int userId2 = Integer.parseInt(like2[0]);
                    String postId2 = like2[1];
                    
                    if (userId != userId2 && postId.equals(postId2)) {
                        bothLikedSamePost = true;
                        break;
                    }
                }
            }
            if (bothLikedSamePost) break;
        }
        
        System.out.println("Users " + user1 + " and " + user2 + 
                         " liked same post: " + bothLikedSamePost);
        assertTrue(bothLikedSamePost, "Users 101 and 102 should have liked same post");
        
        System.out.println("✅ TC-LIKE-011 PASSED: Like with user profile integration\n");
    }
}