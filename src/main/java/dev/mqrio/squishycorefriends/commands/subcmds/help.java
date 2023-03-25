package dev.mqrio.squishycorefriends.commands.subcmds;

import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.paginator.Paginator;
import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.event.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Pattern;

public class help {
    public help(CommandSender sender, String[] args) {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        Player player = (Player) sender;

        if(!player.hasPermission(config.GetConfig().getString("permissions.help"))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.noCommandAccess")));

            return;
        }

        Map<String, String> pluginCommands = new LinkedHashMap<String, String>();
        pluginCommands.put("/friend list [page no.]", "List your friends.");
        pluginCommands.put("/friend toggle", "Toggle your friend requests on/off.");
        pluginCommands.put("/friend togglehugs", "Toggle your ability to be hugged on/off.");
        pluginCommands.put("/friend toggletps", "Toggle your friend teleports on/off.");
        pluginCommands.put("/friend hug [username]", "Hug your friend.");
        pluginCommands.put("/friend add [username]", "Add a player as your friend.");
        pluginCommands.put("/friend remove [username]", "Remove a player from your friends.");
        pluginCommands.put("/friend accept [username]", "Accept a friend request sent by a player.");
        pluginCommands.put("/friend deny [username]", "Deny a friend request sent by a player.");
        pluginCommands.put("/friend ignore [username]", "Ignore a friend request sent by a player.");
        pluginCommands.put("/friend tp [username]", "Teleport to a friend.");
        pluginCommands.put("/friend tpyes [username]", "Accept a teleport request sent by a friend.");
        pluginCommands.put("/friend tpno [username]", "Deny a teleport request sent by a friend.");
        pluginCommands.put("/friend tpignore [username]", "Ignore a teleport request sent by a friend.");
        pluginCommands.put("/friend home [username]", "Teleport to the home of you and your friend.");
        pluginCommands.put("/friend sethome [username]", "Set the location of the home of you and your friend.");

        List<String> helpLines = new ArrayList<String>();

        for (String command : pluginCommands.keySet()) {
            String description = pluginCommands.get(command);

            helpLines.add(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.helpListCommand").replaceAll(Pattern.quote("{command}"), command).replaceAll(Pattern.quote("{description}"), description)));
        }

        String[] helpLinesArray = new String[ helpLines.size() ];
        helpLines.toArray( helpLinesArray );

        String[][] pagination = new Paginator().Paginator(helpLinesArray, 8);

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
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.helpListInvalidPageNumber")));

            return;
        }

        if(pagination.length < page) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.helpListInvalidPageNumber")));

            return;
        }

        final TextComponent.Builder message = TextComponent.builder()
                .content(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.helpListHeader")))
                .append(TextComponent.newline());

        String[] paginationItem = pagination[page - 1];

        for(int i = 0; i < paginationItem.length; i++) {
            message.append(TextComponent.builder(paginationItem[i]));
            message.append(TextComponent.newline());
        }

        message.append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.helpListFooter").replaceAll(Pattern.quote("{current}"), String.valueOf(page)).replaceAll(Pattern.quote("{max}"), String.valueOf(pagination.length)))));
        message.append(TextComponent.newline());

        if(page >= pagination.length) {
            if(page != 1) {
                message.append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.helpListPrevPage")))
                        .clickEvent(ClickEvent.runCommand("/friend help " + String.valueOf(page - 1))));
            }
        } else {
            if(page != 1) {
                message.append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.helpListPrevPage")))
                        .clickEvent(ClickEvent.runCommand("/friend help " + String.valueOf(page - 1))));
                message.append(TextComponent.builder("  |  "));
            }
            message.append(TextComponent.builder(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.helpListNextPage")))
                    .clickEvent(ClickEvent.runCommand("/friend help " + String.valueOf(page + 1))));
        }

        TextComponent messageBuilt = message.build();

        TextAdapter.sendMessage(player, messageBuilt);
    }
}
