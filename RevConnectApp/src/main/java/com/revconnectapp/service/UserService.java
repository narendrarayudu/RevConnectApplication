package com.revconnectapp.service;

import com.revconnectapp.dao.UserDAO;
import com.revconnectapp.model.User;
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
    
    public User getUserById(int id) {
        return userDAO.getById(id);
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