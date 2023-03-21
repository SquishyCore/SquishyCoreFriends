package dev.mqrio.squishycorefriends.integrations;

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class SuperVanish {
    public boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}
