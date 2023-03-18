package dev.mqrio.squishycorefriends.config;

import dev.mqrio.squishycorefriends.SquishyCoreFriends;
import org.bukkit.configuration.file.FileConfiguration;

public class Configuration {
    public void InitConfig() {
        SquishyCoreFriends.getPlugin().saveDefaultConfig();
    }

    public FileConfiguration GetConfig() {
        return SquishyCoreFriends.getPlugin().getConfig();
    }
}