package dev.mqrio.squishycorefriends.commands.subcmds;

import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import dev.mqrio.squishycorefriends.models.Friendship;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

public class home {
    public home(CommandSender sender, String[] args) throws SQLException {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.home"))) {
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

        if( currentFriendship.HomePosX == 0 && currentFriendship.HomePosY == 0 && currentFriendship.HomePosZ == 0 && currentFriendship.HomeWorld.isEmpty() ) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.noFriendshipHome").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)));

            return;
        }

        World world = Bukkit.getWorld(UUID.fromString(currentFriendship.HomeWorld));

        if(world == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.unexpectedError")));

            return;
        }

        Location loc = new Location(world, currentFriendship.HomePosX, currentFriendship.HomePosY, currentFriendship.HomePosZ);

        player.teleport(loc);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.teleportedToHome").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)));
    }
}
