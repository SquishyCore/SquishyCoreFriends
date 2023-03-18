package dev.mqrio.squishycorefriends.commands.subcmds;

import dev.mqrio.squishycorefriends.SquishyCoreFriends;
import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

public class tpyes {
    public tpyes(CommandSender sender, String[] args) throws SQLException {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.tpRequestAccept"))) {
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

        if( SquishyCoreFriends.tpRequestsCache.hasKey(requesterPlayer.uuid + ":" + requesteePlayer.uuid) ) {
            OfflinePlayer requesterBukkitPlayer = Bukkit.getOfflinePlayer(UUID.fromString(requesterPlayer.uuid));

            if( !requesterBukkitPlayer.isOnline() ) {
                player.sendMessage((ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.tpAcceptNoLongerOnline").replaceAll(Pattern.quote("{sender}"), requesterPlayer.username))));
            } else {
                Location currentLocation = player.getLocation();
                requesterBukkitPlayer.getPlayer().teleport(currentLocation);

                requesterBukkitPlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.teleportedAlert_TeleporterVersion").replaceAll(Pattern.quote("{friend}"), player.getName())));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.teleportedAlert_TeleportedToVersion").replaceAll(Pattern.quote("{teleporter}"), requesterPlayer.username)));

                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 127, 255), 5.0F);
                requesterBukkitPlayer.getPlayer().spawnParticle(Particle.REDSTONE, player.getLocation(), 250, dustOptions);
                requesterBukkitPlayer.getPlayer().playSound(requesterBukkitPlayer.getPlayer().getLocation(), Sound.valueOf(config.GetConfig().getString("effects.teleportSound")), config.GetConfig().getInt("effects.teleportSoundVolume"), config.GetConfig().getInt("effects.teleportSoundPitch"));
            }

            SquishyCoreFriends.tpRequestsCache.remove(requesterPlayer.uuid + ":" + requesteePlayer.uuid);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.tpNothingToAccept")));
        }
    }
}
