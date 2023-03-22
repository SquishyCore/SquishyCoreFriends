package dev.mqrio.squishycorefriends.events;

import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import dev.mqrio.squishycorefriends.integrations.SuperVanish;
import dev.mqrio.squishycorefriends.models.Friendship;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class PlayerQuit implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST) // has to be highest for the SuperVanish integration to work properly
    public void playerQuitEvent(PlayerQuitEvent event) throws SQLException {
        Configuration config = new Configuration();

        Player player = event.getPlayer();

        new Actions().UpdatePlayerLastPos(player.getUniqueId().toString(), player.getLocation());

        if( config.GetConfig().getBoolean("alerts.onceFriendLeaves") ) {
            if( config.GetConfig().getBoolean("SuperVanish.respect") ) {
                if(new SuperVanish().isVanished(player)) {
                    return;
                }
            }

            Map<Integer, Friendship> friendships = new Actions().GetFriendships(player.getUniqueId().toString());
            for (Integer key : friendships.keySet()) {
                Friendship friendship = friendships.get(key);

                String otherPlayer;
                if (friendship.firstPlayerUUID.equals(player.getUniqueId().toString())) {
                    otherPlayer = friendship.secondPlayerUUID;
                } else {
                    otherPlayer = friendship.firstPlayerUUID;
                }

                OfflinePlayer otherPlayerBukkitInstance = Bukkit.getOfflinePlayer(UUID.fromString(otherPlayer));
                if (otherPlayerBukkitInstance.isOnline()) {
                    otherPlayerBukkitInstance.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendLeftAlert").replaceAll(Pattern.quote("{friend}"), player.getName())));
                }
            }
        }
    }
}