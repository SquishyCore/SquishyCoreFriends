package dev.mqrio.squishycorefriends.commands.subcmds;

import dev.mqrio.squishycorefriends.SquishyCoreFriends;
import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import dev.mqrio.squishycorefriends.models.Friendship;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class hug {
    public hug(CommandSender sender, String[] args) throws SQLException {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.hug"))) {
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

        OfflinePlayer friendBukkitPlayer = Bukkit.getOfflinePlayer(UUID.fromString(friendPlayer.uuid));

        if(!friendBukkitPlayer.isOnline()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.hugNotOnline").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)));

            return;
        }

        if(!friendPlayer.isHuggable) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.notAcceptingHugs").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)));

            return;
        }

        new Actions().AddFriendshipHug(currentFriendship.firstPlayerUUID, currentFriendship.secondPlayerUUID);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.huggedHuggerVersion").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)));
        friendBukkitPlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.huggedHuggedVersion").replaceAll(Pattern.quote("{hugger}"), player.getName())));
        friendBukkitPlayer.getPlayer().playSound(friendBukkitPlayer.getPlayer().getLocation(), Sound.valueOf(config.GetConfig().getString("effects.hugSound")), config.GetConfig().getInt("effects.hugSoundVolume"), config.GetConfig().getInt("effects.hugSoundPitch"));

        new BukkitRunnable() {
            Location loc = friendBukkitPlayer.getPlayer().getLocation();
            double t = 0;
            double r = 2;
            public void run() {
                t = t + Math.PI / 16;
                double x = r * cos(t);
                double y = r * sin(t);
                double z = r * sin(t);
                loc.add(x, y, z);
                friendBukkitPlayer.getPlayer().getWorld().spawnParticle(Particle.valueOf(config.GetConfig().getString("effects.hugParticles")), loc, 1);
                loc.subtract(x, y, z);
                if(t > Math.PI * 8) {
                    this.cancel();
                }
            }
        }.runTaskTimer(SquishyCoreFriends.getPlugin(), 0, 1);
    }
}
