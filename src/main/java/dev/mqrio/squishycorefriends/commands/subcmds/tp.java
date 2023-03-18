package dev.mqrio.squishycorefriends.commands.subcmds;

import dev.mqrio.squishycorefriends.SquishyCoreFriends;
import dev.mqrio.squishycorefriends.cache.tpRequestsCacheEntry;
import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import dev.mqrio.squishycorefriends.models.Friendship;
import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.event.ClickEvent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public class tp {
    public tp(CommandSender sender, String[] args) throws SQLException {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.tp"))) {
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

        if( config.GetConfig().getBoolean("friendTeleports.instantlyTeleport") ) {
            if (!friendBukkitPlayer.isOnline()) {
                if (!config.GetConfig().getBoolean("friendTeleports.allowOfflineTeleports")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.offlineTpsAreDisabled")));
                } else {
                    World world = Bukkit.getWorld(UUID.fromString(friendPlayer.LastPosWorld));

                    if(world == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.unexpectedError")));

                        return;
                    }

                    Location loc = new Location(world, friendPlayer.LastPosX, friendPlayer.LastPosY, friendPlayer.LastPosZ);

                    player.teleport(loc);

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.teleportedAlert_TeleporterVersion").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)));

                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 127, 255), 5.0F);
                    player.spawnParticle(Particle.REDSTONE, player.getLocation(), 250, dustOptions);
                    player.playSound(player.getLocation(), Sound.valueOf(config.GetConfig().getString("effects.teleportSound")), config.GetConfig().getInt("effects.teleportSoundVolume"), config.GetConfig().getInt("effects.teleportSoundPitch"));
                }
            } else {
                Location friendLocation = friendBukkitPlayer.getPlayer().getLocation();
                player.teleport(friendLocation);

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.teleportedAlert_TeleporterVersion").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)));
                friendBukkitPlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.teleportedAlert_TeleportedToVersion").replaceAll(Pattern.quote("{teleporter}"), player.getName())));

                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 127, 255), 5.0F);
                player.spawnParticle(Particle.REDSTONE, player.getLocation(), 250, dustOptions);
                player.playSound(player.getLocation(), Sound.valueOf(config.GetConfig().getString("effects.teleportSound")), config.GetConfig().getInt("effects.teleportSoundVolume"), config.GetConfig().getInt("effects.teleportSoundPitch"));
            }
        } else {
            if (!friendBukkitPlayer.isOnline()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.offlineTpsAreDisabled")));

                return;
            }

            tpRequestsCacheEntry cEntry = new tpRequestsCacheEntry();
            cEntry.SentAt = Instant.now().getEpochSecond();
            SquishyCoreFriends.tpRequestsCache.put(player.getUniqueId().toString() + ":" + friendPlayer.uuid, cEntry);

            final TextComponent message = TextComponent.builder()
                    .content(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.tpRequestNotification").replaceAll(Pattern.quote("{sender}"), player.getName())))
                    .append(TextComponent.newline())
                    .append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.tpRequestNotificationAccept")))
                            .clickEvent(ClickEvent.runCommand("/friend tpyes " + player.getName())))
                    .append("  |  ")
                    .append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.tpRequestNotificationDeny")))
                            .clickEvent(ClickEvent.runCommand("/friend tpno " + player.getName())))
                    .append("  |  ")
                    .append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.tpRequestNotificationIgnore")))
                            .clickEvent(ClickEvent.runCommand("/friend tpignore " + player.getName())))
                    .build();

            TextAdapter.sendMessage(friendBukkitPlayer.getPlayer(), message);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.tpRequestSent").replaceAll(Pattern.quote("{requestee}"), friendPlayer.username)));
        }
    }
}
