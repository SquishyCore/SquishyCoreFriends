package dev.mqrio.squishycorefriends.commands;

import dev.mqrio.squishycorefriends.SquishyCoreFriends;
import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Database;
import dev.mqrio.squishycorefriends.commands.subcmds.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public class cmdHandler implements CommandExecutor {
    Connection connection = Database.GetDb();
    Configuration config = new Configuration();
    String TablesPrefix = config.GetConfig().getString("database.tables_prefix");

    public cmdHandler() throws SQLException {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Configuration config = new Configuration();
        String pluginPrefix = config.GetConfig().getString("prefix");

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if(!player.hasPermission(config.GetConfig().getString("permissions.use"))) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.noCommandAccess")));

                return true;
            }

            if(args.length <= 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.invalidCommandUsage") + "/friend help"));

                return true;
            }

            String subCommand = args[0].trim();

            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

            switch(subCommand) {
                case "help":
                    new help(sender, subArgs);
                    break;
                case "accept":
                    try {
                        new accept(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "deny":
                    try {
                        new deny(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "ignore":
                    try {
                        new ignore(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "add":
                    try {
                        new add(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "remove":
                    try {
                        new remove(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "home":
                    try {
                        new home(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "sethome":
                    try {
                        new sethome(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "tp":
                    try {
                        new tp(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "tpyes":
                    try {
                        new tpyes(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "tpno":
                    try {
                        new tpno(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "tpignore":
                    try {
                        new tpignore(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "hug":
                    try {
                        new hug(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "list":
                    try {
                        new list(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "toggle":
                    try {
                        new toggle(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "togglehugs":
                    try {
                        new togglehugs(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "toggletps":
                    try {
                        new toggletps(sender, subArgs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', pluginPrefix + config.GetConfig().getString("locale.invalidCommandUsage") + "/friend help"));
            }

            return true;
        }

        SquishyCoreFriends.getPlugin().getLogger().info(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.onlyPlayerUsable")));
        return true;
    }
}