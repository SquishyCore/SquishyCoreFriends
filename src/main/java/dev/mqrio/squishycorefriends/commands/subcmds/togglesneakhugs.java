package dev.mqrio.squishycorefriends.commands.subcmds;

import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class togglesneakhugs {
    public togglesneakhugs(CommandSender sender, String[] args) throws SQLException {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.toggleSneakHugs"))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.noCommandAccess")));

            return;
        }

        dev.mqrio.squishycorefriends.models.Player pluginPlayer = new Actions().GetPlayer(player.getUniqueId().toString(), 0);

        if( pluginPlayer.allowsRightClickHugs ) {
            new Actions().DisableRightClickHugs(pluginPlayer.uuid);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.toggledSneakHugsOff")));
        } else {
            new Actions().EnableRightClickHugs(pluginPlayer.uuid);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.toggledSneakHugsOn")));
        }
    }
}
