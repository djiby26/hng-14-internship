package com.hng.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {
    private static Connection connection = null;

    public Connexion() {}

    public static Connection getConnection() {
//        if (connection == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/hng_db";
                String user = "pape";
                String password = "1234";

                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
//        }
        return connection;
    }
}
