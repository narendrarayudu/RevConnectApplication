package com.revconnectapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;

public class TestLogging {
    
    private static final Logger logger = LogManager.getLogger(TestLogging.class);
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Testing Log4j2 File Logging ===");
        System.out.println("Working directory: " + new File(".").getAbsolutePath());
        
        // 1. Create logs directory if it doesn't exist
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            boolean created = logsDir.mkdir();
            if (created) {
                System.out.println("✓ Created logs directory");
            } else {
                System.out.println("✗ Failed to create logs directory");
                System.out.println("Check permissions for: " + logsDir.getAbsolutePath());
            }
        } else {
            System.out.println("✓ Logs directory already exists");
        }
        
        // 2. Check write permissions
        System.out.println("Can write to logs directory: " + logsDir.canWrite());
        
        // 3. Test logging at different levels
        System.out.println("\n--- Writing Log Messages ---");
        
        logger.trace("This is a TRACE message");
        logger.debug("This is a DEBUG message");
        logger.info("This is an INFO message");
        logger.warn("This is a WARN message");
        logger.error("This is an ERROR message");
        logger.fatal("This is a FATAL message");
        
        // 4. Test parameterized logging
        String username = "john_doe";
        int userId = 123;
        logger.info("User '{}' with ID {} logged in", username, userId);
        
        // 5. Test exception logging
        try {
            throw new RuntimeException("Simulated exception for testing");
        } catch (RuntimeException e) {
            logger.error("An error occurred during testing", e);
        }
        
        // 6. Simulate application activity
        System.out.println("\n--- Simulating Application Activity ---");
        for (int i = 1; i <= 3; i++) {
            logger.info("Processing batch #{} of 3", i);
            Thread.sleep(500); // Half second delay
        }
        
        // 7. Force flush and shutdown Log4j2
        System.out.println("\n--- Finalizing Logs ---");
        LogManager.shutdown();
        
        // 8. Check if files were created
        Thread.sleep(1000); // Wait for file write
        
        File appLog = new File("logs/app.log");
        File errorLog = new File("logs/error.log");
        
        System.out.println("\n--- Checking Log Files ---");
        System.out.println("logs/app.log exists: " + appLog.exists());
        System.out.println("logs/error.log exists: " + errorLog.exists());
        
        if (appLog.exists()) {
            System.out.println("logs/app.log size: " + appLog.length() + " bytes");
        }
        if (errorLog.exists()) {
            System.out.println("logs/error.log size: " + errorLog.length() + " bytes");
        }
        
        // 9. Show file locations
        System.out.println("\n--- File Locations ---");
        System.out.println("Project location: " + new File(".").getAbsolutePath());
        System.out.println("Expected log files at:");
        System.out.println("  - " + appLog.getAbsolutePath());
        System.out.println("  - " + errorLog.getAbsolutePath());
        
        System.out.println("\n=== Test Complete ===");
        System.out.println("Check the 'logs' folder in your project directory!");
    }
}