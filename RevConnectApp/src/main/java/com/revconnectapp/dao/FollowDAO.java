package com.revconnectapp.dao;

import com.revconnectapp.model.User;
import com.revconnectapp.util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowDAO {
    
    public boolean follow(int followerId, int followedId) {
        String sql = "INSERT INTO follows (follower_id, followed_id) VALUES (?, ?)";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followedId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean unfollow(int followerId, int followedId) {
        String sql = "DELETE FROM follows WHERE follower_id = ? AND followed_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followedId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isFollowing(int followerId, int followedId) {
        String sql = "SELECT 1 FROM follows WHERE follower_id = ? AND followed_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followedId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<User> getFollowers(int userId) {
        List<User> followers = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                    "INNER JOIN follows f ON u.id = f.follower_id " +
                    "WHERE f.followed_id = ? ORDER BY f.created_at DESC";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                followers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return followers;
    }
    
    public List<User> getFollowing(int userId) {
        List<User> following = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                    "INNER JOIN follows f ON u.id = f.followed_id " +
                    "WHERE f.follower_id = ? ORDER BY f.created_at DESC";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                following.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return following;
    }
    
    public int getFollowerCount(int userId) {
        String sql = "SELECT COUNT(*) FROM follows WHERE followed_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getFollowingCount(int userId) {
        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // ✅ NEW METHODS:
    
    public List<User> getMutualFollowers(int userId1, int userId2) {
        List<User> mutuals = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                    "INNER JOIN follows f1 ON u.id = f1.follower_id " +
                    "INNER JOIN follows f2 ON u.id = f2.follower_id " +
                    "WHERE f1.followed_id = ? AND f2.followed_id = ? AND u.id != ? AND u.id != ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId1);
            stmt.setInt(4, userId2);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                mutuals.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mutuals;
    }
    
    public List<User> getSuggestedUsers(int userId) {
        List<User> suggestions = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM users u " +
                    "INNER JOIN follows f1 ON u.id = f1.followed_id " +
                    "INNER JOIN follows f2 ON f1.follower_id = f2.follower_id " +
                    "WHERE f2.followed_id = ? AND u.id != ? " +
                    "AND NOT EXISTS (SELECT 1 FROM follows f3 WHERE f3.follower_id = ? AND f3.followed_id = u.id) " +
                    "LIMIT 10";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                suggestions.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suggestions;
    }
    
    public List<User> getPopularUsers(int limit) {
        List<User> popular = new ArrayList<>();
        String sql = "SELECT u.*, COUNT(f.id) as follower_count FROM users u " +
                    "LEFT JOIN follows f ON u.id = f.followed_id " +
                    "WHERE u.user_type IN ('CREATOR', 'BUSINESS') " +
                    "GROUP BY u.id ORDER BY follower_count DESC LIMIT ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                popular.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return popular;
    }
    
    public List<User> getNonFollowingUsers(int userId) {
        List<User> nonFollowing = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                    "WHERE u.id != ? " +
                    "AND NOT EXISTS (SELECT 1 FROM follows f WHERE f.follower_id = ? AND f.followed_id = u.id) " +
                    "AND u.user_type IN ('CREATOR', 'BUSINESS') " +
                    "LIMIT 20";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                nonFollowing.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nonFollowing;
    }
    
    public List<User> getRecentFollowers(int userId, int limit) {
        List<User> recent = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                    "INNER JOIN follows f ON u.id = f.follower_id " +
                    "WHERE f.followed_id = ? " +
                    "ORDER BY f.created_at DESC LIMIT ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                recent.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recent;
    }
}