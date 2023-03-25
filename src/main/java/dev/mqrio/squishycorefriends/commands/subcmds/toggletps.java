package dev.mqrio.squishycorefriends.commands.subcmds;

import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class toggletps {
    public toggletps(CommandSender sender, String[] args) throws SQLException {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.toggleTps"))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.noCommandAccess")));

            return;
        }

        dev.mqrio.squishycorefriends.models.Player pluginPlayer = new Actions().GetPlayer(player.getUniqueId().toString(), 0);

        if( pluginPlayer.isTeleportable ) {
            new Actions().DisableTeleports(pluginPlayer.uuid);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.toggledTpsOff")));
        } else {
            new Actions().EnableTeleports(pluginPlayer.uuid);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.toggledTpsOn")));
        }
    }
}
