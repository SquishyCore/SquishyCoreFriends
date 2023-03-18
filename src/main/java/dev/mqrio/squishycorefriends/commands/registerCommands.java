package dev.mqrio.squishycorefriends.commands;

import dev.mqrio.squishycorefriends.SquishyCoreFriends;

import java.sql.SQLException;

public class registerCommands {
    public void Register() throws SQLException {
        SquishyCoreFriends.getPlugin().getCommand("friend").setExecutor(new cmdHandler());
        SquishyCoreFriends.getPlugin().getCommand("friend").setTabCompleter(new TabCompletion());

        SquishyCoreFriends.getPlugin().getCommand("f").setExecutor(new cmdHandler());
        SquishyCoreFriends.getPlugin().getCommand("f").setTabCompleter(new TabCompletion());

        SquishyCoreFriends.getPlugin().getCommand("friends").setExecutor(new cmdHandler());
        SquishyCoreFriends.getPlugin().getCommand("friends").setTabCompleter(new TabCompletion());

        SquishyCoreFriends.getPlugin().getCommand("friendsreload").setExecutor(new pluginReloader());
    }
}