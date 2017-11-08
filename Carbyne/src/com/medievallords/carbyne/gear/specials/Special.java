package com.medievallords.carbyne.gear.specials;

import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Special {

    String getSpecialName();

    void callSpecial();

    default void broadcastMessage(String radiusMessage, Location centerPoint, int radius) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getLocation().distance(centerPoint) < radius) {
                MessageManager.sendMessage(onlinePlayer, radiusMessage);
            }
        }
    }
}