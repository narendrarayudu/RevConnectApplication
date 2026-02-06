package com.revconnectapp.service;

import com.revconnectapp.dao.CommentDAO;
import com.revconnectapp.model.Comment;
import java.util.List;

public class CommentService {
    private CommentDAO commentDAO = new CommentDAO();
    
    public void addComment(int postId, int userId, String content) {
        commentDAO.addComment(postId, userId, content);
    }
    
    public List<Comment> getComments(int postId) {
        return commentDAO.getComments(postId);
    }
    
    public List<Comment> getMyComments(int postId, int userId) {
        return commentDAO.getMyComments(postId, userId);
    }
    
    public void deleteComment(int commentId) {
        commentDAO.deleteComment(commentId);
    }
    
    public int getCommentCount(int postId) {
        return commentDAO.getCommentCount(postId);
    }
}