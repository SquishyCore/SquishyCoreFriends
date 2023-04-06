package dev.mqrio.squishycorefriends.database;

import dev.mqrio.squishycorefriends.config.Configuration;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static dev.mqrio.squishycorefriends.database.Database.GetDb;

public class InitTables {
    public void Init() throws SQLException {
        Configuration config = new Configuration();
        String TablesPrefix = config.GetConfig().getString("database.tables_prefix");

        String sql = "CREATE TABLE IF NOT EXISTS " + TablesPrefix + "friendships (`id` BIGINT NOT NULL AUTO_INCREMENT, `friend1` BIGINT, `friend2` BIGINT, `home_x` DOUBLE, `home_y` DOUBLE, `home_z` DOUBLE, `home_world` TEXT, `hugs_count` BIGINT, `since` BIGINT, PRIMARY KEY(id));";
        try {
            PreparedStatement stmt = GetDb().prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Failed to initialize tables");
        }

        sql = "CREATE TABLE IF NOT EXISTS " + TablesPrefix + "players (`id` BIGINT NOT NULL AUTO_INCREMENT, `uuid` varchar(255), `username` TEXT, `is_accepting` INT, `is_huggable` INT, `is_teleportable` INT, `allows_rightclick_hugs` INT, `lastpos_x` DOUBLE, `lastpos_y` DOUBLE, `lastpos_z` DOUBLE, `lastpos_world` TEXT, `max_friends` BIGINT, PRIMARY KEY(id, uuid));";
        try {
            PreparedStatement stmt = GetDb().prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Failed to initialize tables");
        }
    }
}