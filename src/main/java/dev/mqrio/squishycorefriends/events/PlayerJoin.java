package dev.mqrio.squishycorefriends.events;

import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import dev.mqrio.squishycorefriends.integrations.SuperVanish;
import dev.mqrio.squishycorefriends.models.Friendship;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class PlayerJoin implements Listener {
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) throws SQLException {
        Configuration config = new Configuration();

        Player player = event.getPlayer();

        new Actions().NewPlayer(player.getUniqueId().toString(), player.getName());
        new Actions().UpdatePlayer(player.getUniqueId().toString(), player.getName());

        if( config.GetConfig().getBoolean("alerts.onceFriendJoins") ) {
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
                    otherPlayerBukkitInstance.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendJoinedAlert").replaceAll(Pattern.quote("{friend}"), player.getName())));
                    otherPlayerBukkitInstance.getPlayer().playSound(otherPlayerBukkitInstance.getPlayer().getLocation(), Sound.valueOf(config.GetConfig().getString("effects.friendJoinSound")), config.GetConfig().getInt("effects.friendJoinSoundVolume"), config.GetConfig().getInt("effects.friendJoinSoundPitch"));
                }
            }
        }
    }
}