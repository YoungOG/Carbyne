package com.medievallords.carbyne.gear.specials;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MainSpecial {

    public void broadcastRadius(Location center, int radius) {

    }

    private ArrayList<Player> getRadius(Location radiusCenter, int radius) {
        ArrayList<Player> playerList = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getLocation().distance(radiusCenter) < radius) {
                playerList.add(onlinePlayer);
            }
        }
        return playerList;
    }
}
