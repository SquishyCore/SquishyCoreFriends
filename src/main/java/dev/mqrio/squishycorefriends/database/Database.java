package dev.mqrio.squishycorefriends.database;

import dev.mqrio.squishycorefriends.config.Configuration;

import java.sql.*;

public class Database {
    static Connection connection;

    public static void InitDb() throws SQLException {
        Configuration config = new Configuration();
        String hostname = config.GetConfig().getString("database.hostname");
        String username = config.GetConfig().getString("database.username");
        String password = config.GetConfig().getString("database.password");
        String dbName = config.GetConfig().getString("database.name");
        String port = config.GetConfig().getString("database.port");
        String url = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?autoReconnect=true";

        connection = DriverManager.getConnection(url, username, password);

        new InitTables().Init();
    }

    public void CloseDb() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection GetDb() throws SQLException {
        try {
            String sql = "SELECT VERSION();";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeQuery();
        } catch(Exception e) {
            InitDb();
        }
        return connection;
    }
}