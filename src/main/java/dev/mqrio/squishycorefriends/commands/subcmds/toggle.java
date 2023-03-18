package dev.mqrio.squishycorefriends.commands.subcmds;

import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class toggle {
    public toggle(CommandSender sender, String[] args) throws SQLException {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.toggle"))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.noCommandAccess")));

            return;
        }

        dev.mqrio.squishycorefriends.models.Player pluginPlayer = new Actions().GetPlayer(player.getUniqueId().toString(), 0);

        if( pluginPlayer.isAccepting ) {
            new Actions().DisableAccepting(pluginPlayer.uuid);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.toggledRequestsOff")));
        } else {
            new Actions().EnableAccepting(pluginPlayer.uuid);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.toggledRequestsOn")));
        }
    }
}
