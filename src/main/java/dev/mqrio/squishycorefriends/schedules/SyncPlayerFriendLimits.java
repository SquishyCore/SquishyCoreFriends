package dev.mqrio.squishycorefriends.schedules;

import dev.mqrio.squishycorefriends.SquishyCoreFriends;
import dev.mqrio.squishycorefriends.database.Actions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class SyncPlayerFriendLimits {
    public SyncPlayerFriendLimits() throws SQLException {
    }

    public void SyncPlayerFriendLimits() {
        SquishyCoreFriends.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(SquishyCoreFriends.getPlugin(), new Runnable() {
            @Override
            public void run() {

                Bukkit.getScheduler().runTask(SquishyCoreFriends.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        // > Asynchronous tasks should never access any API in Bukkit.
                        // ... https://bukkit.fandom.com/wiki/Scheduler_Programming#Tips_for_thread_safety

                        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                            try {
                                new Actions().SyncPlayerFriendsLimit(player);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });

            }
        }, 600L, 600L);
    }
}
