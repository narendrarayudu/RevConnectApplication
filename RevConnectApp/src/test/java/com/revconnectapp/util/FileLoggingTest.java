package com.revconnectapp.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileLoggingTest {
    
    private static final Logger logger = LogManager.getLogger(FileLoggingTest.class);
    
    @Test
    @DisplayName("Test Basic File Logging")
    public void testBasicFileLogging() throws IOException, InterruptedException {
        System.out.println("=== Testing Basic File Logging ===");
        
        // Ensure logs directory exists
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdir();
            System.out.println("Created logs directory");
        }
        
        // Log some test messages
        logger.info("=== STARTING FILE LOGGING TEST ===");
        logger.debug("This is a DEBUG message to file");
        logger.info("This is an INFO message to file");
        logger.warn("This is a WARN message to file");
        logger.error("This is an ERROR message to file");
        
        // Force flush (Log4j2 might buffer)
        LogManager.shutdown();
        
        // Wait a bit for file write
        Thread.sleep(1000);
        
        // Check if files exist
        File appLog = new File("logs/app.log");
        File errorLog = new File("logs/error.log");
        
        System.out.println("Checking log files:");
        System.out.println("logs/app.log exists: " + appLog.exists());
        System.out.println("logs/error.log exists: " + errorLog.exists());
        
        // Read and display file contents
        if (appLog.exists()) {
            System.out.println("\n=== Contents of app.log ===");
            List<String> appLines = Files.readAllLines(Paths.get("logs/app.log"));
            for (String line : appLines) {
                System.out.println(line);
            }
        }
        
        if (errorLog.exists()) {
            System.out.println("\n=== Contents of error.log ===");
            List<String> errorLines = Files.readAllLines(Paths.get("logs/error.log"));
            for (String line : errorLines) {
                System.out.println(line);
            }
        }
        
        // Verify
        assertTrue(appLog.exists() || errorLog.exists(), 
                  "At least one log file should exist");
        
        System.out.println("✅ Basic file logging test completed");
    }
    
    @Test
    @DisplayName("Test Log File Creation and Writing")
    public void testLogFileCreation() throws IOException, InterruptedException {
        System.out.println("\n=== Testing Log File Creation ===");
        
        // Delete existing log files to start fresh
        File appLog = new File("logs/app.log");
        File errorLog = new File("logs/error.log");
        
        if (appLog.exists()) appLog.delete();
        if (errorLog.exists()) errorLog.delete();
        
        System.out.println("Deleted existing log files");
        
        // Log messages
        logger.info("Test message 1");
        logger.error("Test error message 1");
        logger.info("Test message 2");
        logger.error("Test error message 2");
        
        // Force flush
        LogManager.shutdown();
        Thread.sleep(1000);
        
        // Verify files were created
        assertTrue(appLog.exists(), "app.log should be created");
        assertTrue(errorLog.exists(), "error.log should be created");
        
        // Check file sizes
        long appSize = appLog.length();
        long errorSize = errorLog.length();
        
        System.out.println("app.log size: " + appSize + " bytes");
        System.out.println("error.log size: " + errorSize + " bytes");
        
        assertTrue(appSize > 0, "app.log should not be empty");
        assertTrue(errorSize > 0, "error.log should not be empty");
        
        // error.log should be smaller (only ERROR messages)
        assertTrue(errorSize < appSize, 
                  "error.log should be smaller than app.log (only ERROR messages)");
        
        System.out.println("✅ Log file creation test completed");
    }
    
    @Test
    @DisplayName("Test Different Log Levels in Files")
    public void testLogLevelsInFiles() throws IOException, InterruptedException {
        System.out.println("\n=== Testing Log Levels in Files ===");
        
        // Create test log
        File testLog = new File("logs/test-levels.log");
        if (testLog.exists()) testLog.delete();
        
        // Log different levels
        logger.trace("TRACE message - should not appear in files");
        logger.debug("DEBUG message - might appear if level is DEBUG");
        logger.info("INFO message - should appear");
        logger.warn("WARN message - should appear");
        logger.error("ERROR message - should appear");
        logger.fatal("FATAL message - should appear");
        
        // Test exception logging
        try {
            throw new RuntimeException("Test exception for file logging");
        } catch (RuntimeException e) {
            logger.error("Exception occurred in test", e);
        }
        
        LogManager.shutdown();
        Thread.sleep(1000);
        
        System.out.println("Logged messages at all levels");
        System.out.println("✅ Log levels test completed");
    }
    
    @Test
    @DisplayName("Test Multiple Logger Categories")
    public void testMultipleLoggerCategories() throws InterruptedException {
        System.out.println("\n=== Testing Multiple Logger Categories ===");
        
        // Create loggers for different parts of your app
        Logger userDaoLogger = LogManager.getLogger("com.revconnectapp.dao.UserDAO");
        Logger postDaoLogger = LogManager.getLogger("com.revconnectapp.dao.PostDAO");
        Logger serviceLogger = LogManager.getLogger("com.revconnectapp.service.UserService");
        
        // Log with different loggers
        userDaoLogger.info("UserDAO: Retrieving user by ID 123");
        userDaoLogger.debug("UserDAO: SQL: SELECT * FROM users WHERE id = 123");
        
        postDaoLogger.info("PostDAO: Creating new post");
        postDaoLogger.warn("PostDAO: Post content is very long");
        
        serviceLogger.info("UserService: Processing user registration");
        serviceLogger.error("UserService: Registration failed - email already exists");
        
        LogManager.shutdown();
        Thread.sleep(1000);
        
        System.out.println("Logged with different logger categories");
        System.out.println("✅ Multiple logger categories test completed");
    }
}