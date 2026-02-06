package com.revconnectapp.dao;

import com.revconnectapp.model.Profile;
import com.revconnectapp.util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileDAO {
    public Profile createOrUpdate(Profile profile) {
        String sql = """
            INSERT INTO profiles (user_id, name, bio, picture_path, location, website, privacy) 
            VALUES (?, ?, ?, ?, ?, ?, ?) 
            ON DUPLICATE KEY UPDATE 
            name=VALUES(name), bio=VALUES(bio), picture_path=VALUES(picture_path),
            location=VALUES(location), website=VALUES(website), privacy=VALUES(privacy)
            """;
        
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getName());
            stmt.setString(3, profile.getBio());
            stmt.setString(4, profile.getPicturePath());
            stmt.setString(5, profile.getLocation());
            stmt.setString(6, profile.getWebsite());
            stmt.setString(7, profile.getPrivacy());
            
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) profile.setId(rs.getInt(1));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profile;
    }

    public Profile getByUserId(int userId) {
        String sql = "SELECT * FROM profiles WHERE user_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Profile profile = new Profile();
                profile.setId(rs.getInt("id"));
                profile.setUserId(rs.getInt("user_id"));
                profile.setName(rs.getString("name"));
                profile.setBio(rs.getString("bio"));
                profile.setPicturePath(rs.getString("picture_path"));
                profile.setLocation(rs.getString("location"));
                profile.setWebsite(rs.getString("website"));
                profile.setPrivacy(rs.getString("privacy"));
                return profile;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Profile> searchProfiles(String query) {
        List<Profile> profiles = new ArrayList<>();
        String sql = """
            SELECT p.*, u.username 
            FROM profiles p 
            JOIN users u ON p.user_id = u.id 
            WHERE u.username LIKE ? OR p.name LIKE ? OR p.location LIKE ?
            """;
        
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String q = "%" + query + "%";
            stmt.setString(1, q);
            stmt.setString(2, q);
            stmt.setString(3, q);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Profile profile = new Profile();
                profile.setId(rs.getInt("id"));
                profile.setUserId(rs.getInt("user_id"));
                profile.setName(rs.getString("name"));
                profile.setBio(rs.getString("bio"));
                profile.setLocation(rs.getString("location"));
                // Add username to profile if needed
                profiles.add(profile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profiles;
    }
}
