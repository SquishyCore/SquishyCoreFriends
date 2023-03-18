package dev.mqrio.squishycorefriends.commands.subcmds;

import dev.mqrio.squishycorefriends.SquishyCoreFriends;
import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

public class accept {
    public accept(CommandSender sender, String[] args) throws SQLException {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.requestAccept"))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.noCommandAccess")));

            return;
        }

        if(args.length <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.invalidCommandUsage") + "/friend help"));

            return;
        }

        String requester = args[0].trim();
        String requesterUUID = new Actions().UsernameToUUID(requester);

        if( requesterUUID == null ) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.invalidPlayerUsername")));

            return;
        }

        dev.mqrio.squishycorefriends.models.Player requesteePlayer = new Actions().GetPlayer(player.getUniqueId().toString(), 0);
        dev.mqrio.squishycorefriends.models.Player requesterPlayer = new Actions().GetPlayer(requesterUUID, 0);

        if( SquishyCoreFriends.requestsCache.hasKey(requesterPlayer.uuid + ":" + requesteePlayer.uuid) ) {
            Integer friendRequestResult = new Actions().NewFriendship(requesterPlayer.uuid, requesteePlayer.uuid);

            if( friendRequestResult == -1 ) {
                OfflinePlayer requesterBukkitPlayer = Bukkit.getOfflinePlayer(UUID.fromString(requesterPlayer.uuid));

                if (requesterBukkitPlayer.isOnline()) {
                    requesterBukkitPlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.acceptedRequestRequesterVersion").replaceAll(Pattern.quote("{requestee}"), requesteePlayer.username)));
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.acceptedRequestRequesteeVersion").replaceAll(Pattern.quote("{sender}"), requesterPlayer.username)));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.unexpectedError")));
            }

            SquishyCoreFriends.requestsCache.remove(requesterPlayer.uuid + ":" + requesteePlayer.uuid);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.nothingToAccept")));
        }
    }
}
