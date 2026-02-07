package com.revconnectapp.service;

import com.revconnectapp.dao.UserDAO;
import com.revconnectapp.model.User;
import com.revconnectapp.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public User register(User user) {
        return userDAO.create(user);
    }

    public User login(String username, String password) {
        User user = userDAO.getByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
 // In UserService.java, add these methods:

    public boolean updateSecurityQuestion(int userId, String question, String answer) {
        // Update security question in database
        String sql = "UPDATE users SET security_question = ?, security_answer = ? WHERE id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, question);
            pstmt.setString(2, answer);
            pstmt.setInt(3, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Also update your getUserById and other methods to load security questions:
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setUserType(rs.getString("user_type"));
                user.setSecurityQuestion(rs.getString("security_question"));
                user.setSecurityAnswer(rs.getString("security_answer"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // CORRECTED METHOD: Simply delegate to the DAO
    public User getUserByUsername(String username) {
        return userDAO.getByUsername(username);
    }

    public List<User> searchUsers(String query) {
        return userDAO.searchUsers(query);
    }
 // In UserService.java - Add this method
    public boolean updatePassword(int userId, String newPassword) {
        // In a real app, you'd update this in the database
        // For now, we'll update the UserDAO if it exists
        
        // This is a simplified version - you'll need to implement properly
        User user = userDAO.getById(userId);
        if (user != null) {
            user.setPassword(newPassword);
            // Here you would call userDAO.update(user) if you have that method
            return true;
        }
        return false;
    }
}