package com.revconnectapp.ui;

import com.revconnectapp.model.Profile;
import com.revconnectapp.model.User;
import com.revconnectapp.service.ProfileService;
import com.revconnectapp.util.InputUtil;
import java.util.List;

public class ProfileMenu {
    private final ProfileService profileService = new ProfileService();

    public void show(User currentUser) {
        Profile myProfile = profileService.getProfile(currentUser.getId());
        if (myProfile == null) {
            System.out.println("👤 No profile yet! Let's create one.");
            createProfile(currentUser.getId());
            myProfile = profileService.getProfile(currentUser.getId());
        }

        System.out.println("\n=== 👤 MY PROFILE ===");
        System.out.println(myProfile);
        
        String choice = InputUtil.getChoice("Edit Profile", "View Others", "Search Profiles", "Back");
        
        switch (choice) {
            case "Edit Profile" -> editProfile(currentUser.getId());
            case "View Others" -> viewOtherProfiles(currentUser.getId());
            case "Search Profiles" -> searchProfiles();
            default -> {} // Back to dashboard
        }
    }

    private void createProfile(int userId) {
        System.out.println("\n📝 CREATE PROFILE");
        Profile profile = new Profile();
        profile.setUserId(userId);
        
        System.out.print("Full Name: "); profile.setName(InputUtil.getString());
        System.out.print("Bio (optional): "); profile.setBio(InputUtil.getString());
        System.out.print("Location: "); profile.setLocation(InputUtil.getString());
        System.out.print("Website (optional): "); profile.setWebsite(InputUtil.getString());
        profile.setPrivacy("PUBLIC");
        
        profileService.createOrUpdate(profile);
        System.out.println("✅ Profile created!");
    }

    private void editProfile(int userId) {
        Profile profile = profileService.getProfile(userId);
        if (profile == null) {
            createProfile(userId);
            return;
        }

        System.out.println("\n✏️ EDIT PROFILE");
        System.out.println("Leave blank to keep current value.");
        
        System.out.print("Name [" + profile.getName() + "]: "); 
        String name = InputUtil.getString();
        if (!name.isEmpty()) profile.setName(name);
        
        System.out.print("Bio [" + safePreview(profile.getBio()) + "]: "); 
        String bio = InputUtil.getString();
        if (!bio.isEmpty()) profile.setBio(bio);
        
        System.out.print("Location [" + profile.getLocation() + "]: "); 
        String location = InputUtil.getString();
        if (!location.isEmpty()) profile.setLocation(location);
        
        System.out.print("Website [" + profile.getWebsite() + "]: "); 
        String website = InputUtil.getString();
        if (!website.isEmpty()) profile.setWebsite(website);
        
        profileService.createOrUpdate(profile);
        System.out.println("✅ Profile updated!");
    }

    private void viewOtherProfiles(int currentUserId) {
        System.out.println("\n👥 OTHER PROFILES (Search to view)");
        searchProfiles();
    }

    private void searchProfiles() {
        System.out.print("\n🔍 Search by name/username/location: ");
        String query = InputUtil.getString();
        
        List<Profile> profiles = profileService.searchProfiles(query);
        if (profiles.isEmpty()) {
            System.out.println("No profiles found.");
        } else {
            System.out.println("\n👥 PROFILES:");
            for (int i = 0; i < profiles.size(); i++) {
                System.out.println((i+1) + ". " + profiles.get(i).getName());
            }
        }
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private String safePreview(String text) {
        if (text == null || text.length() <= 50) return text;
        return text.substring(0, 50) + "...";
    }
}
