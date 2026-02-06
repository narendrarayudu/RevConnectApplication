package com.revconnectapp.service;

import com.revconnectapp.dao.PostDAO;
import com.revconnectapp.model.Post;
import java.util.List;
import java.util.ArrayList;

public class PostService {
    private PostDAO postDAO = new PostDAO();
    
    public void createPost(Post post) {
        postDAO.createPost(post);
    }
    
    public List<Post> getUserFeed(int userId) {
        return postDAO.getUserPosts(userId);
    }
    public void editPost(int postId, String content, String hashtags) {
        postDAO.editPost(postId, content, hashtags);
    }

    public void deletePost(int postId) {
        postDAO.deletePost(postId);
    }

    public List<Post> getConnectionsFeed(int userId) {
        return postDAO.getConnectionsFeed(userId);
    }

    public Post getPostById(int postId) {
        return postDAO.getPostById(postId);
    }
     
 // In PostService.java
    public List<Post> getAllPublicPosts() {
        // This should return all posts from database
        // For now, get posts from all users
        
        List<Post> allPosts = new ArrayList<>();
        
        // You'll need to implement this properly in your PostDAO
        // For now, you can combine posts from multiple users
        
        // Example: Get posts from first 20 users
        for (int i = 1; i <= 20; i++) {
            List<Post> userPosts = getUserFeed(i);
            if (userPosts != null) {
                allPosts.addAll(userPosts);
            }
        }
        
        return allPosts;
    }
}
