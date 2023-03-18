package dev.mqrio.squishycorefriends.commands.subcmds;

import dev.mqrio.squishycorefriends.SquishyCoreFriends;
import dev.mqrio.squishycorefriends.cache.requestsCacheEntry;
import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import dev.mqrio.squishycorefriends.models.Friendship;
import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public class add {
    public add(CommandSender sender, String[] args) throws SQLException {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.add"))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.noCommandAccess")));

            return;
        }

        if(args.length <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.invalidCommandUsage") + "/friend help"));

            return;
        }

        String requestee = args[0].trim();
        String requesteeUUID = new Actions().UsernameToUUID(requestee);

        if( requesteeUUID == null ) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.invalidPlayerUsername")));

            return;
        }

        dev.mqrio.squishycorefriends.models.Player requesteePlayer = new Actions().GetPlayer(requesteeUUID, 0);
        dev.mqrio.squishycorefriends.models.Player requesterPlayer = new Actions().GetPlayer(player.getUniqueId().toString(), 0);

        if(requesteePlayer.uuid.equals(requesterPlayer.uuid)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.cantFriendYourself")));

            return;
        }

        Friendship currentFriendship = new Actions().GetFriendship(requesteePlayer.uuid, requesterPlayer.uuid);
        if( currentFriendship.firstPlayerUUID != null ) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.alreadyFriends")));

            return;
        }

        if( SquishyCoreFriends.requestsCache.hasKey(requesterPlayer.uuid + ":" + requesteePlayer.uuid) ) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.requestAlreadyOngoing")));

            return;
        }

        if( SquishyCoreFriends.requestsCache.hasKey(requesteePlayer.uuid + ":" + requesterPlayer.uuid) ) {
            player.performCommand("friend accept " + requesteePlayer.username);

            return;
        }

        Boolean isRequesteeOnline = true;

        if(!Bukkit.getOfflinePlayer(UUID.fromString(requesteePlayer.uuid)).isOnline()) {
            if( !config.GetConfig().getBoolean("allowOfflineFriendRequests") ) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.offlineRequestsAreDisallowed").replaceAll(Pattern.quote("{username}"), requesteePlayer.username)));

                return;
            }

            isRequesteeOnline = false;
        }

        if(!requesteePlayer.isAccepting) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.notAcceptingRequests").replaceAll(Pattern.quote("{username}"), requesteePlayer.username)));

            return;
        }

        Integer requesterFriendsCount = new Actions().GetPlayerFriendshipsCount(requesterPlayer.uuid);
        Integer requesteeFriendsCount = new Actions().GetPlayerFriendshipsCount(requesteePlayer.uuid);

        if( requesteeFriendsCount >= requesteePlayer.maxFriends ) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.requesteeCantAcceptRequests").replaceAll(Pattern.quote("{username}"), requesteePlayer.username)));

            return;
        }

        if( requesterFriendsCount >= requesterPlayer.maxFriends ) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.requesterReachedMaxFriends")));

            return;
        }

        if(isRequesteeOnline) {
            Player requesteeBukkitPlayer = Bukkit.getPlayer(UUID.fromString(requesteePlayer.uuid));

            requestsCacheEntry cEntry = new requestsCacheEntry();
            cEntry.SentAt = Instant.now().getEpochSecond();
            SquishyCoreFriends.requestsCache.put(requesterPlayer.uuid + ":" + requesteePlayer.uuid, cEntry);

            final TextComponent message = TextComponent.builder()
                    .content(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendRequestNotificationWhileOnline").replaceAll(Pattern.quote("{sender}"), requesterPlayer.username)))
                    .append(TextComponent.newline())
                    .append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendRequestNotificationWhileOnlineAccept")))
                            .clickEvent(ClickEvent.runCommand("/friend accept " + requesterPlayer.username)))
                    .append("  |  ")
                    .append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendRequestNotificationWhileOnlineDeny")))
                            .clickEvent(ClickEvent.runCommand("/friend deny " + requesterPlayer.username)))
                    .append("  |  ")
                    .append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendRequestNotificationWhileOnlineIgnore")))
                            .clickEvent(ClickEvent.runCommand("/friend ignore " + requesterPlayer.username)))
                    .build();

            TextAdapter.sendMessage(requesteeBukkitPlayer, message);
        } else {
            // TODO: offline friend requests
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.friendRequestSent").replaceAll(Pattern.quote("{requestee}"), requesteePlayer.username)));
    }
}
