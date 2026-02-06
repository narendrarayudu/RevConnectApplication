package com.revconnectapp.service;

import com.revconnectapp.dao.ProfileDAO;
import com.revconnectapp.model.Profile;
import java.util.List;

public class ProfileService {
    private final ProfileDAO profileDAO = new ProfileDAO();

    public Profile createOrUpdate(Profile profile) {
        return profileDAO.createOrUpdate(profile);
    }

    public Profile getProfile(int userId) {
        return profileDAO.getByUserId(userId);
    }

    public List<Profile> searchProfiles(String query) {
        return profileDAO.searchProfiles(query);
    }

    public int getUnreadCount(int userId) {
        return 0; // Placeholder - NotificationService handles this
    }
}
