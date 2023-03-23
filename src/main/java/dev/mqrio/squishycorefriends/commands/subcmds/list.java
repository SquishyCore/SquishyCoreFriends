package dev.mqrio.squishycorefriends.commands.subcmds;

import de.myzelyam.api.vanish.VanishAPI;
import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import dev.mqrio.squishycorefriends.models.Friendship;
import dev.mqrio.squishycorefriends.paginator.Paginator;
import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class list {
    public list(CommandSender sender, String[] args) throws SQLException {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.list"))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.noCommandAccess")));

            return;
        }

        List<String> friendsLines = new ArrayList<String>();

        Map<Integer, Friendship> friendships = new Actions().GetFriendships(player.getUniqueId().toString());
        for (Integer key : friendships.keySet()) {
            Friendship friendship = friendships.get(key);

            String otherPlayer;
            if (friendship.firstPlayerUUID.equals(player.getUniqueId().toString())) {
                otherPlayer = friendship.secondPlayerUUID;
            } else {
                otherPlayer = friendship.firstPlayerUUID;
            }

            dev.mqrio.squishycorefriends.models.Player friendPlayer = new Actions().GetPlayer(otherPlayer, 0);

            OfflinePlayer friendBukkitPlayer = Bukkit.getOfflinePlayer(UUID.fromString(friendPlayer.uuid));

            if(friendBukkitPlayer.isOnline()) {
                if( config.GetConfig().getBoolean("SuperVanish.respect") ) {
                    if( Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish") ) {
                        if( !VanishAPI.canSee(player, friendBukkitPlayer.getPlayer()) ) {
                            friendsLines.add(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListLineOffline").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)) + "|METADATA:" + friendPlayer.username);
                        } else {
                            friendsLines.add(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListLineOnline").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)) + "|METADATA:" + friendPlayer.username);
                        }
                    } else {
                        friendsLines.add(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListLineOnline").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)) + "|METADATA:" + friendPlayer.username);
                    }
                } else {
                    friendsLines.add(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListLineOnline").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)) + "|METADATA:" + friendPlayer.username);
                }
            } else {
                friendsLines.add(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListLineOffline").replaceAll(Pattern.quote("{friend}"), friendPlayer.username)) + "|METADATA:" + friendPlayer.username);
            }
        }

        String[] friendsLinesArray = new String[ friendsLines.size() ];
        friendsLines.toArray( friendsLinesArray );

        String[][] pagination = new Paginator().Paginator(friendsLinesArray, 8);

        int page = 1;

        if(args.length >= 1) {
            try {
                page = Integer.parseInt(args[0].trim());
            } catch(Exception e) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.invalidCommandUsage") + "/friend help"));

                return;
            }
        }

        if(page <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.friendListInvalidPageNumber")));

            return;
        }

        if(pagination.length <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.friendListNonetoShow")));

            return;
        }

        if(pagination.length < page) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.friendListInvalidPageNumber")));

            return;
        }

        final TextComponent.Builder message = TextComponent.builder()
                .content(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListHeader")))
                .append(TextComponent.newline());

        String[] paginationItem = pagination[page - 1];

        for(int i = 0; i < paginationItem.length; i++) {
            String friendLine = paginationItem[i].split(Pattern.quote("|METADATA:"))[0];
            String playerUsername = paginationItem[i].split(Pattern.quote("|METADATA:"))[1];
            message.append(TextComponent.builder(friendLine));
            message.append(TextComponent.builder(" જ⁀➴ "));
            message.append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListHug")))
                    .clickEvent(ClickEvent.runCommand("/friend hug " + playerUsername)));
            message.append(TextComponent.builder(" | "));
            message.append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListTeleport")))
                    .clickEvent(ClickEvent.runCommand("/friend tp " + playerUsername)));
            message.append(TextComponent.builder(" | "));
            message.append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListHome")))
                    .clickEvent(ClickEvent.runCommand("/friend home " + playerUsername)));
            message.append(TextComponent.newline());
        }

        message.append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListFooter").replaceAll(Pattern.quote("{current}"), String.valueOf(page)).replaceAll(Pattern.quote("{max}"), String.valueOf(pagination.length)))));
        message.append(TextComponent.newline());

        if(page >= pagination.length) {
            if(page != 1) {
                message.append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListPrevPage")))
                        .clickEvent(ClickEvent.runCommand("/friend list " + String.valueOf(page - 1))));
            }
        } else {
            if(page != 1) {
                message.append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListPrevPage")))
                        .clickEvent(ClickEvent.runCommand("/friend list " + String.valueOf(page - 1))));
                message.append(TextComponent.builder("  |  "));
            }
            message.append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.friendListNextPage")))
                    .clickEvent(ClickEvent.runCommand("/friend list " + String.valueOf(page + 1))));
        }

        TextComponent messageBuilt = message.build();

        TextAdapter.sendMessage(player, messageBuilt);
    }
}
