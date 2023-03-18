package dev.mqrio.squishycorefriends.schedules;

import dev.mqrio.squishycorefriends.SquishyCoreFriends;
import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public class CheckTeleportRequestsExpiry {
    public CheckTeleportRequestsExpiry() throws SQLException {
    }

    public void CheckTeleportRequestsExpiry() {
        SquishyCoreFriends.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(SquishyCoreFriends.getPlugin(), new Runnable() {
            @Override
            public void run() {

                Bukkit.getScheduler().runTask(SquishyCoreFriends.getPlugin(), new Runnable() {
                    @Override
                    public void run() {

                        Configuration config = new Configuration();
                        String pluginPrefix = config.GetConfig().getString("prefix");

                        for (String key : SquishyCoreFriends.tpRequestsCache.keySet()) {
                            if( (Instant.now().getEpochSecond() - SquishyCoreFriends.tpRequestsCache.get(key).SentAt) >= config.GetConfig().getLong("friendTeleports.requestsExpiry") ) {
                                String requesterUUID = key.split(":")[0];
                                String requesteeUUID = key.split(":")[1];

                                dev.mqrio.squishycorefriends.models.Player requesteePlayer = null;
                                dev.mqrio.squishycorefriends.models.Player requesterPlayer = null;
                                try {
                                    requesteePlayer = new Actions().GetPlayer(requesteeUUID, 0);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                try {
                                    requesterPlayer = new Actions().GetPlayer(requesterUUID, 0);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }

                                OfflinePlayer requesterBukkitPlayer = Bukkit.getOfflinePlayer(UUID.fromString(requesterUUID));
                                OfflinePlayer requesteeBukkitPlayer = Bukkit.getOfflinePlayer(UUID.fromString(requesteeUUID));

                                if( requesterBukkitPlayer.isOnline() ) {
                                    requesterBukkitPlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.tpRequestExpiredRequesterVersion").replaceAll(Pattern.quote("{requestee}"), requesteePlayer.username)));
                                }

                                if( requesteeBukkitPlayer.isOnline() ) {
                                    requesteeBukkitPlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.tpRequestExpiredRequesteeVersion").replaceAll(Pattern.quote("{sender}"), requesterPlayer.username)));
                                }

                                SquishyCoreFriends.tpRequestsCache.remove(key);
                            }
                        }

                    }
                });

            }
        }, 20L, 20L);
    }
}
