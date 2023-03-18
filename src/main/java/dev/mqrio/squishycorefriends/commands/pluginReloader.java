package dev.mqrio.squishycorefriends.commands;

import dev.mqrio.squishycorefriends.SquishyCoreFriends;
import dev.mqrio.squishycorefriends.config.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class pluginReloader implements CommandExecutor {
    Configuration config = new Configuration();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if(!player.hasPermission(config.GetConfig().getString("permissions.reload")) && !player.isOp()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.noCommandAccess")));

                return true;
            }

            SquishyCoreFriends.setIsReloading(true);
            /*
            This bit is bugged, we can't reload a plugin this way without causing much trouble.

            SquishyCoreFriends.getPlugin().getPluginLoader().disablePlugin(SquishyCoreFriends.getPlugin());
            SquishyCoreFriends.getPlugin().getPluginLoader().enablePlugin(SquishyCoreFriends.getPlugin());
            */
            SquishyCoreFriends.getPlugin().reloadConfig();
            SquishyCoreFriends.getPlugin().saveConfig();
            SquishyCoreFriends.setIsReloading(false);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.reloadedPlugin")));

            return true;
        }

        SquishyCoreFriends.setIsReloading(true);
        /*
        This bit is bugged, we can't reload a plugin this way without causing much trouble.

        SquishyCoreFriends.getPlugin().getPluginLoader().disablePlugin(SquishyCoreFriends.getPlugin());
        SquishyCoreFriends.getPlugin().getPluginLoader().enablePlugin(SquishyCoreFriends.getPlugin());
        */
        SquishyCoreFriends.getPlugin().reloadConfig();
        SquishyCoreFriends.getPlugin().saveConfig();
        SquishyCoreFriends.setIsReloading(false);

        SquishyCoreFriends.getPlugin().getLogger().info(ChatColor.translateAlternateColorCodes('&', config.GetConfig().getString("locale.reloadedPlugin")));
        return true;
    }
}