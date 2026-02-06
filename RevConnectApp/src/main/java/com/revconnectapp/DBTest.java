package com.revconnectapp;

import com.revconnectapp.util.ConnectionUtil;

public class DBTest {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        ConnectionUtil.testConnection();
    }
}
