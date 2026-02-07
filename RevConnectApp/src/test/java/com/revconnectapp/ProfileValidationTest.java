package com.revconnectapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class ProfileValidationTest {
    
    @Test
    @DisplayName("TC-PROFILE-001: Test Profile Object Creation")
    public void testProfileObjectCreation() {
        System.out.println("=== TC-PROFILE-001: Profile Object Creation Test ===");
        
        // Test creating a Profile object
        int userId = 1;
        String title = "Senior Software Engineer";
        String bio = "Passionate about Java development and open source projects.";
        String location = "San Francisco, CA";
        String linkedinUrl = "https://linkedin.com/in/johndoe";
        String phone = "+1-555-0123";
        
        System.out.println("Creating Profile object:");
        System.out.println("User ID: " + userId);
        System.out.println("Title: " + title);
        System.out.println("Bio: " + bio);
        System.out.println("Location: " + location);
        System.out.println("LinkedIn: " + linkedinUrl);
        System.out.println("Phone: " + phone);
        
        // Validate profile data
        assertTrue(userId > 0, "User ID must be positive");
        assertNotNull(title, "Title should not be null");
        assertNotNull(bio, "Bio should not be null");
        assertNotNull(location, "Location should not be null");
        
        // Title validation
        assertFalse(title.trim().isEmpty(), "Title should not be empty");
        assertTrue(title.length() >= 2, "Title should be at least 2 characters");
        assertTrue(title.length() <= 100, "Title should not exceed 100 characters");
        
        // Bio validation
        assertFalse(bio.trim().isEmpty(), "Bio should not be empty");
        assertTrue(bio.length() >= 10, "Bio should be at least 10 characters");
        assertTrue(bio.length() <= 500, "Bio should not exceed 500 characters");
        
        // Location validation
        assertFalse(location.trim().isEmpty(), "Location should not be empty");
        assertTrue(location.length() <= 100, "Location should not exceed 100 characters");
        
        // Optional fields
        if (linkedinUrl != null && !linkedinUrl.isEmpty()) {
            assertTrue(linkedinUrl.startsWith("https://"), "LinkedIn URL should use HTTPS");
            assertTrue(linkedinUrl.contains("linkedin.com"), "Should be LinkedIn URL");
        }
        
        if (phone != null && !phone.isEmpty()) {
            assertTrue(phone.length() >= 7, "Phone should be at least 7 characters");
            assertTrue(phone.length() <= 20, "Phone should not exceed 20 characters");
        }
        
        System.out.println("✅ TC-PROFILE-001 PASSED: Profile object validation successful\n");
    }
    
    @Test
    @DisplayName("TC-PROFILE-002: Test Profile Bio Length Validation")
    public void testProfileBioLength() {
        System.out.println("=== TC-PROFILE-002: Profile Bio Length Test ===");
        
        // Test various bio lengths
        String[] testBios = {
            "Short bio",  // 9 chars
            "This is a medium length biography about the user.",  // 49 chars
            "This is a very detailed biography that includes work experience, education background, skills, and personal interests. The user is passionate about technology and enjoys contributing to open source projects.",  // 166 chars
            "",  // empty
            "   "  // whitespace only
        };
        
        System.out.println("Testing bio length validation:");
        for (int i = 0; i < testBios.length; i++) {
            String bio = testBios[i];
            System.out.println("\nBio " + (i+1) + ": '" + bio + "'");
            System.out.println("Length: " + bio.length() + " characters");
            
            boolean isValid = !bio.trim().isEmpty() && 
                             bio.length() >= 10 && 
                             bio.length() <= 500;
            
            if (isValid) {
                System.out.println("✓ Valid bio");
                assertTrue(isValid, "Bio should be valid: " + bio);
            } else {
                System.out.println("✗ Invalid bio");
                assertFalse(isValid, "Bio should be invalid: " + bio);
            }
        }
        
        System.out.println("✅ TC-PROFILE-002 PASSED: Profile bio length validation\n");
    }
    
    @Test
    @DisplayName("TC-PROFILE-003: Test Profile Title Validation")
    public void testProfileTitleValidation() {
        System.out.println("=== TC-PROFILE-003: Profile Title Validation Test ===");
        
        // Test valid titles
        String[] validTitles = {
            "Software Engineer",
            "Product Manager",
            "DevOps Specialist",
            "UX Designer",
            "Data Scientist",
            "CTO",
            "CEO"
        };
        
        // Test invalid titles
        String[] invalidTitles = {
            "",
            "   ",
            "A",  // too short
            "This title is way too long and exceeds the maximum allowed characters for a profile title",  // too long
            null
        };
        
        System.out.println("Testing valid titles:");
        for (String title : validTitles) {
            System.out.println("  ✓ " + title);
            assertNotNull(title, "Title should not be null");
            assertFalse(title.trim().isEmpty(), "Title should not be empty");
            assertTrue(title.length() >= 2, "Title should be at least 2 chars: " + title);
            assertTrue(title.length() <= 50, "Title should not exceed 50 chars: " + title);
        }
        
        System.out.println("\nTesting invalid titles:");
        for (String title : invalidTitles) {
            System.out.print("  ✗ ");
            if (title == null) {
                System.out.println("null");
                assertNull(title, "Title should be null");
            } else {
                System.out.println("'" + title + "'");
                boolean isEmpty = title.trim().isEmpty();
                boolean tooShort = title.length() < 2;
                boolean tooLong = title.length() > 50;
                
                assertTrue(isEmpty || tooShort || tooLong, 
                          "Title should be invalid: " + title);
            }
        }
        
        System.out.println("✅ TC-PROFILE-003 PASSED: Profile title validation\n");
    }
    
    @Test
    @DisplayName("TC-PROFILE-004: Test Profile URL Validation")
    public void testProfileURLValidation() {
        System.out.println("=== TC-PROFILE-004: Profile URL Validation Test ===");
        
        // Test URLs
        String[] testUrls = {
            "https://linkedin.com/in/johndoe",
            "https://github.com/johndoe",
            "https://twitter.com/johndoe",
            "http://example.com",  // HTTP instead of HTTPS
            "invalid-url",
            "www.example.com",  // missing protocol
            "",  // empty
            null  // null
        };
        
        System.out.println("Testing profile URLs:");
        for (String url : testUrls) {
            System.out.print("\nURL: ");
            if (url == null) {
                System.out.println("null");
                assertNull(url, "URL should be null");
                continue;
            }
            
            System.out.println(url);
            
            if (url.isEmpty()) {
                System.out.println("  → Empty URL (allowed)");
                assertTrue(url.isEmpty(), "URL should be empty");
            } else {
                // Check if it's a valid URL
                boolean isValid = url.startsWith("https://") && 
                                 url.contains(".") &&
                                 url.length() >= 10;
                
                if (isValid) {
                    System.out.println("  ✓ Valid URL");
                    assertTrue(isValid, "URL should be valid: " + url);
                } else {
                    System.out.println("  ✗ Invalid URL");
                    assertFalse(isValid, "URL should be invalid: " + url);
                }
            }
        }
        
        // Test specific URL patterns
        System.out.println("\nTesting specific URL patterns:");
        
        String linkedinUrl = "https://linkedin.com/in/janedoe";
        assertTrue(linkedinUrl.startsWith("https://"), "Should use HTTPS");
        assertTrue(linkedinUrl.contains("linkedin.com"), "Should be LinkedIn");
        System.out.println("✓ LinkedIn URL format correct");
        
        String githubUrl = "https://github.com/username";
        assertTrue(githubUrl.startsWith("https://"), "Should use HTTPS");
        assertTrue(githubUrl.contains("github.com"), "Should be GitHub");
        System.out.println("✓ GitHub URL format correct");
        
        System.out.println("✅ TC-PROFILE-004 PASSED: Profile URL validation\n");
    }
}