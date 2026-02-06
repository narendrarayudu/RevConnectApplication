package com.revconnectapp.service;

import com.revconnectapp.dao.LikeDAO;

public class LikeService {
    private LikeDAO likeDAO = new LikeDAO();
    
    public void likePost(int postId, int userId) {
        likeDAO.likePost(postId, userId);
    }
    
    public void unlikePost(int postId, int userId) {
        likeDAO.unlikePost(postId, userId);
    }
    
    public boolean isLikedByUser(int postId, int userId) {
        return likeDAO.isLiked(postId, userId);
    }
    
    public int getLikeCount(int postId) {
        return likeDAO.getLikeCount(postId);
    }
}