package com.revconnectapp.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {
    
    // Private constructor to prevent instantiation
    private LogUtil() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * Get logger for the calling class
     */
    public static Logger getLogger() {
        // Get the calling class name (skip LogUtil in stack trace)
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[2].getClassName();
        return LogManager.getLogger(className);
    }
    
    /**
     * Get logger with specified name
     */
    public static Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }
    
    /**
     * Get logger for specified class
     */
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
    
    /**
     * Log method entry
     */
    public static void logMethodEntry(Logger logger, String methodName, Object... params) {
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("ENTER: ").append(methodName).append("(");
            for (int i = 0; i < params.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(params[i]);
            }
            sb.append(")");
            logger.debug(sb.toString());
        }
    }
    
    /**
     * Log method exit
     */
    public static void logMethodExit(Logger logger, String methodName, Object result) {
        if (logger.isDebugEnabled()) {
            logger.debug("EXIT: {} -> {}", methodName, result);
        }
    }
    
    /**
     * Log method exit (void)
     */
    public static void logMethodExit(Logger logger, String methodName) {
        if (logger.isDebugEnabled()) {
            logger.debug("EXIT: {}", methodName);
        }
    }
    
    /**
     * Log SQL query (sanitized)
     */
    public static void logSQL(Logger logger, String sql, Object... params) {
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("SQL: ").append(sql);
            if (params != null && params.length > 0) {
                sb.append(" [Params: ");
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(maskPassword(params[i]));
                }
                sb.append("]");
            }
            logger.debug(sb.toString());
        }
    }
    
    /**
     * Mask passwords in log output
     */
    private static Object maskPassword(Object param) {
        if (param == null) return null;
        String str = param.toString();
        if (str.toLowerCase().contains("password") || str.toLowerCase().contains("pass")) {
            return "******";
        }
        return param;
    }
    
    /**
     * Log business operation
     */
    public static void logBusinessOperation(Logger logger, String operation, String entity, Object id) {
        logger.info("Business Operation: {} - {} [ID: {}]", operation, entity, id);
    }
    
    /**
     * Log error with context
     */
    public static void logError(Logger logger, String context, Throwable throwable) {
        logger.error("Error in {}: {}", context, throwable.getMessage(), throwable);
    }
    
    /**
     * Log warning with context
     */
    public static void logWarning(Logger logger, String context, String message) {
        logger.warn("Warning in {}: {}", context, message);
    }
    
    /**
     * Log performance metrics
     */
    public static void logPerformance(Logger logger, String operation, long startTime, long endTime) {
        long duration = endTime - startTime;
        String level = "INFO";
        if (duration > 1000) {
            level = "WARN";
        } else if (duration > 5000) {
            level = "ERROR";
        }
        
        switch (level) {
            case "WARN":
                logger.warn("Performance: {} took {} ms", operation, duration);
                break;
            case "ERROR":
                logger.error("Performance: {} took {} ms (SLOW!)", operation, duration);
                break;
            default:
                logger.info("Performance: {} took {} ms", operation, duration);
        }
    }
}