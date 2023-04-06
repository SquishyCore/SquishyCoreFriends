package dev.mqrio.squishycorefriends.events;

import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.database.Actions;
import dev.mqrio.squishycorefriends.models.Friendship;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.sql.SQLException;

public class PlayerRightClickEvent implements Listener {
    @EventHandler
    public void playerRightClickEvent(PlayerInteractEntityEvent event) throws SQLException {
        Configuration config = new Configuration();

        if(!config.GetConfig().getBoolean("allowRightClickHugging")) {
            return;
        }

        if(!event.getHand().equals(EquipmentSlot.HAND)) {
            /* https://www.spigotmc.org/threads/playerinteractentityevent-firing-two-times.127519/#post-1460018 */
            return;
        }

        if(event.getRightClicked() instanceof Player) {
            Player clicked = (Player) event.getRightClicked();
            Player clicker = event.getPlayer();

            if(!clicker.isSneaking()) {
                return;
            }

            dev.mqrio.squishycorefriends.models.Player pluginPlayer = new Actions().GetPlayer(clicker.getUniqueId().toString(), 0);
            if(!pluginPlayer.allowsRightClickHugs) {
                return;
            }

            dev.mqrio.squishycorefriends.models.Player friendPlayer = new Actions().GetPlayer(clicked.getUniqueId().toString(), 0);

            Friendship currentFriendship = new Actions().GetFriendship(clicker.getUniqueId().toString(), friendPlayer.uuid);
            if( currentFriendship.firstPlayerUUID == null ) {
                return;
            }

            clicker.performCommand("friend hug " + friendPlayer.username);
        }
    }
}