package dev.mqrio.squishycorefriends.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
        @Override
        public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args) {
            if(args.length == 1) {
                List<String> completions = new ArrayList<>();
                completions.add("help");
                completions.add("add");
                completions.add("remove");
                completions.add("accept");
                completions.add("deny");
                completions.add("ignore");
                completions.add("sethome");
                completions.add("home");
                completions.add("tp");
                completions.add("tpyes");
                completions.add("tpno");
                completions.add("tpignore");
                completions.add("hug");
                completions.add("list");
                completions.add("toggle");
                completions.add("togglehugs");
                completions.add("toggletps");

                return completions;
            }

            return null;
        }
}
