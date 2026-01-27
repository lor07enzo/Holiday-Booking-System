package com.lorenzo.pelone.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    
    private static String db_url;
    private static String db_user;
    private static String db_password;
    private static boolean initialized = false;

    private DatabaseConfig() {}

    public static void init(String configPath) {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(configPath)) {
            props.load(fis);
            db_url = props.getProperty("DB_URL");
            db_user = props.getProperty("DB_USER");
            db_password = props.getProperty("DB_PASSWORD");

            initialized = true;
        } catch (IOException e) {
            System.err.println("Error: props file not found: " + configPath);
            throw new RuntimeException("Error: props file not found: " + configPath, e);
        }

    }

    public static Connection getConnection() throws SQLException {

        if (!initialized) {
            throw new RuntimeException("DatabaseConfig not initialized. Call init() first.");
        }
        return DriverManager.getConnection(db_url, db_user, db_password);
    }
}
