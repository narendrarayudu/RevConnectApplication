package com.revconnectapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/revconnectdb?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";  // CHANGE THIS
    private static final String PASS = "123456789";  // CHANGE THIS

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connected successfully!");
                System.out.println("DB: " + conn.getCatalog());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            System.err.println("Check MySQL service, credentials, and database 'revconnectdb'");
        }
        return false;
    }
}
