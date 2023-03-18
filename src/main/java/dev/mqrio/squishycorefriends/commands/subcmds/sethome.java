package dev.mqrio.squishycorefriends.commands.subcmds;

import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import dev.mqrio.squishycorefriends.models.Friendship;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class sethome {
    public sethome(CommandSender sender, String[] args) throws SQLException {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.sethome"))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.noCommandAccess")));

            return;
        }

        if(args.length <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.invalidCommandUsage") + "/friend help"));

            return;
        }

        String friend = args[0].trim();
        String friendUUID = new Actions().UsernameToUUID(friend);

        if( friendUUID == null ) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.invalidPlayerUsername")));

            return;
        }

        dev.mqrio.squishycorefriends.models.Player friendPlayer = new Actions().GetPlayer(friendUUID, 0);

        Friendship currentFriendship = new Actions().GetFriendship(player.getUniqueId().toString(), friendPlayer.uuid);
        if( currentFriendship.firstPlayerUUID == null ) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.notFriends")));

            return;
        }

        Location currentLocation = player.getLocation();

        Integer setHomeResult = new Actions().SetFriendshipHomeLocation(player.getUniqueId().toString(), friendPlayer.uuid, currentLocation);

        if( setHomeResult == -1 ) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.setHome").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.unexpectedError")));
        }
    }
}
