package com.medievallords.carbyne.utils.glaedr.listeners;

import com.medievallords.carbyne.utils.glaedr.scoreboards.PlayerScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScoreboardListeners implements Listener {

    /**
     * EventHandler that creates player scoreboards on join
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerScoreboard playerScoreboard = PlayerScoreboard.getScoreboard(player);

        if (playerScoreboard == null) {
            new PlayerScoreboard(player);
        } else {
            player.setScoreboard(playerScoreboard.getScoreboard());
        }
    }
}