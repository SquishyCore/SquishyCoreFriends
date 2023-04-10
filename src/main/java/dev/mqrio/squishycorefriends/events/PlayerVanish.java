package dev.mqrio.squishycorefriends.events;

import de.myzelyam.api.vanish.PlayerHideEvent;
import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import dev.mqrio.squishycorefriends.models.Friendship;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class PlayerVanish implements Listener {
    @EventHandler
    public void playerVanishEvent(PlayerHideEvent event) throws SQLException {
        Configuration config = new Configuration();

        if( !config.GetConfig().getBoolean("SuperVanish.respect") ) {
            return;
        }

        if( !config.GetConfig().getBoolean("SuperVanish.sendFakeFriendJoinLeaveMessages") ) {
            return;
        }

        Player player = event.getPlayer();

        if( config.GetConfig().getBoolean("alerts.onceFriendJoins") ) {
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