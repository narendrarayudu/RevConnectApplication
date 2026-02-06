package com.revconnectapp.dao;

import com.revconnectapp.model.Post;
import com.revconnectapp.util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {
    
    public void createPost(Post post) {
        String sql = "INSERT INTO posts (user_id, content, hashtags) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getContent());
            stmt.setString(3, post.getHashtags());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void editPost(int postId, String newContent, String newHashtags) {
        String sql = "UPDATE posts SET content = ?, hashtags = ? WHERE id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newContent);
            stmt.setString(2, newHashtags);
            stmt.setInt(3, postId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deletePost(int postId) {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Post> getUserPosts(int userId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE user_id = ? ORDER BY created_at DESC LIMIT 10";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setContent(rs.getString("content"));
                post.setHashtags(rs.getString("hashtags"));
                post.setCreatedAt(rs.getString("created_at"));
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }
    
    // 🔥 CONNECTIONS FEED - Posts from connections + follows
    public List<Post> getConnectionsFeed(int userId) {
        List<Post> feed = new ArrayList<>();
        // SIMPLIFIED - My posts + placeholder for future connections
        String sql = """
            SELECT p.*, u.username FROM posts p 
            JOIN users u ON p.user_id = u.id 
            WHERE p.user_id = ?
            ORDER BY p.created_at DESC LIMIT 20
            """;
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setContent(rs.getString("content"));
                post.setHashtags(rs.getString("hashtags"));
                post.setCreatedAt(rs.getString("created_at"));
                post.setUsername(rs.getString("username"));
                feed.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feed;
    }

    public Post getPostById(int postId) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setContent(rs.getString("content"));
                post.setHashtags(rs.getString("hashtags"));
                post.setCreatedAt(rs.getString("created_at"));
                return post;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
