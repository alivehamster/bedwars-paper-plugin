package com.nxweb.bedwars;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.io.File;

public class DatabaseManager {
    private Connection connection;

    public void connect() throws SQLException, ClassNotFoundException {
        // Create directory structure if it doesn't exist
        String DB_PATH = "plugins/Bedwars/database.db";
        File databaseFile = new File(DB_PATH);
        File parentDir = databaseFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
