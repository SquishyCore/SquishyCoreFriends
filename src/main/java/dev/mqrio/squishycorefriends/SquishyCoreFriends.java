package dev.mqrio.squishycorefriends;

import dev.mqrio.squishycorefriends.cache.requestsCache;
import dev.mqrio.squishycorefriends.cache.tpRequestsCache;
import dev.mqrio.squishycorefriends.commands.registerCommands;
import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Database;
import dev.mqrio.squishycorefriends.events.*;
import dev.mqrio.squishycorefriends.schedules.CheckFriendRequestsExpiry;
import dev.mqrio.squishycorefriends.schedules.SyncPlayerFriendLimits;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class SquishyCoreFriends extends JavaPlugin {
    private static SquishyCoreFriends instance;
    private static boolean isReloading = false;

    Configuration config = new Configuration();
    Database db = new Database();

    // Initialize caches
    public static requestsCache requestsCache = new requestsCache();
    public static tpRequestsCache tpRequestsCache = new tpRequestsCache();

    public static SquishyCoreFriends getPlugin() {
        return instance;
    }

    public static void setIsReloading(boolean value) {
        isReloading = value;
    }

    @Override
    public void onEnable() {
        instance = this;

        config.InitConfig();
        try {
            db.InitDb();
        } catch (SQLException e) {
            Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.dbConnectionError")));
            getServer().getPluginManager().disablePlugin(this);

            return;
        }

        try {
            new registerCommands().Register();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Events
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new PlayerVanish(), this);
        getServer().getPluginManager().registerEvents(new PlayerUnvanish(), this);
        getServer().getPluginManager().registerEvents(new PlayerRightClickEvent(), this);

        // Initialize schedulers
        try {
            new SyncPlayerFriendLimits().SyncPlayerFriendLimits();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            new CheckFriendRequestsExpiry().CheckFriendRequestsExpiry();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.enabledPlugin")));
    }

    @Override
    public void onDisable() {
        db.CloseDb();

        Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.disabledPlugin")));

        if(!isReloading) {
            instance = null;
        }
    }
}
