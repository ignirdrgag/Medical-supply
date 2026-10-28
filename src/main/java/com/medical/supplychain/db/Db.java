package com.medical.supplychain.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Db {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/medisupply?useSSL=false&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties properties = new Properties();
        try (InputStream input = Db.class.getResourceAsStream("/db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            System.err.println("Impossible de lire db.properties : " + exception.getMessage());
        }

        URL = firstNonBlank(System.getenv("DB_URL"), properties.getProperty("db.url"), DEFAULT_URL);
        USER = firstNonBlank(System.getenv("DB_USER"), properties.getProperty("db.user"), DEFAULT_USER);
        PASSWORD = firstNonBlank(System.getenv("DB_PASSWORD"), properties.getProperty("db.password"),
                DEFAULT_PASSWORD);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Pilote MySQL introuvable dans le classpath.", exception);
        }
    }

    private Db() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String firstNonBlank(String first, String second, String fallback) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return fallback;
    }
}
