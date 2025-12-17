package org.example.restaurantmanagement.db;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private final Dotenv dotenv = Dotenv.load();

    private String URL = dotenv.get("JDBC_URL");
    private String USER = dotenv.get("USER");
    private String PASSWORD = dotenv.get("PASSWORD");

    public Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
