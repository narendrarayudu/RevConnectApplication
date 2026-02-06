package com.revconnectapp.service;

import com.revconnectapp.dao.FollowDAO;
import com.revconnectapp.model.User;
import java.util.List;

public class FollowService {
    private FollowDAO followDAO = new FollowDAO();
    private NotificationService notificationService = new NotificationService();
    private UserService userService = new UserService();
    
    // ✅ Follow a user (for creators/business accounts)
    public boolean follow(int followerId, int followedId) {
        // Check if trying to follow self
        if (followerId == followedId) {
            System.out.println("❌ You cannot follow yourself!");
            return false;
        }
        
        // Get user info
        User follower = userService.getUserById(followerId);
        User followed = userService.getUserById(followedId);
        
        // Check if already following
        if (isFollowing(followerId, followedId)) {
            System.out.println("⚠️ You are already following " + followed.getUsername());
            return false;
        }
        
        // Follow the user
        boolean success = followDAO.follow(followerId, followedId);
        
        if (success) {
            System.out.println("✅ Now following " + followed.getUsername() + "!");
            
            // Send notification to the followed user
            notificationService.notifyNewFollower(followedId, follower.getUsername());
        }
        
        return success;
    }
    
    // ✅ Unfollow a user
    public boolean unfollow(int followerId, int followedId) {
        // Check if actually following
        if (!isFollowing(followerId, followedId)) {
            User followed = userService.getUserById(followedId);
            System.out.println("⚠️ You are not following " + followed.getUsername());
            return false;
        }
        
        boolean success = followDAO.unfollow(followerId, followedId);
        
        if (success) {
            User followed = userService.getUserById(followedId);
            System.out.println("✅ Unfollowed " + followed.getUsername() + "!");
        }
        
        return success;
    }
    
    // ✅ Check if user is following another user
    public boolean isFollowing(int followerId, int followedId) {
        return followDAO.isFollowing(followerId, followedId);
    }
    
    // ✅ Get all followers of a user
    public List<User> getFollowers(int userId) {
        return followDAO.getFollowers(userId);
    }
    
    // ✅ Get all users that a user is following
    public List<User> getFollowing(int userId) {
        return followDAO.getFollowing(userId);
    }
    
    // ✅ Get follower count
    public int getFollowerCount(int userId) {
        return followDAO.getFollowerCount(userId);
    }
    
    // ✅ Get following count
    public int getFollowingCount(int userId) {
        return followDAO.getFollowingCount(userId);
    }
    
    // ✅ NEW: Get mutual followers (users who follow each other)
    public List<User> getMutualFollowers(int userId1, int userId2) {
        return followDAO.getMutualFollowers(userId1, userId2);
    }
    
    // ✅ NEW: Get suggested users to follow (users followed by your followers)
    public List<User> getSuggestedUsers(int userId) {
        return followDAO.getSuggestedUsers(userId);
    }
    
    // ✅ NEW: Toggle follow (if following, unfollow; if not following, follow)
    public boolean toggleFollow(int followerId, int followedId) {
        if (isFollowing(followerId, followedId)) {
            return unfollow(followerId, followedId);
        } else {
            return follow(followerId, followedId);
        }
    }
    
    // ✅ NEW: Check if two users follow each other
    public boolean isMutualFollow(int userId1, int userId2) {
        return isFollowing(userId1, userId2) && isFollowing(userId2, userId1);
    }
    
    // ✅ NEW: Get popular users (most followers)
    public List<User> getPopularUsers(int limit) {
        return followDAO.getPopularUsers(limit);
    }
    
    // ✅ NEW: Get non-following users (users you don't follow yet)
    public List<User> getNonFollowingUsers(int userId) {
        return followDAO.getNonFollowingUsers(userId);
    }
    
    // ✅ NEW: Get recent followers
    public List<User> getRecentFollowers(int userId, int limit) {
        return followDAO.getRecentFollowers(userId, limit);
    }
    
    // ✅ NEW: Check if user can follow another user (business logic)
    public boolean canFollow(int followerId, int followedId) {
        // Can't follow yourself
        if (followerId == followedId) {
            return false;
        }
        
        // Check if already following
        if (isFollowing(followerId, followedId)) {
            return false;
        }
        
        // Get user types
        User follower = userService.getUserById(followerId);
        User followed = userService.getUserById(followedId);
        
        // Personal users can only follow CREATOR or BUSINESS accounts
        // CREATOR/BUSINESS accounts can follow anyone
        if (follower.getUserType().equals("PERSONAL")) {
            return followed.getUserType().equals("CREATOR") || followed.getUserType().equals("BUSINESS");
        }
        
        // CREATOR and BUSINESS can follow anyone
        return true;
    }
}