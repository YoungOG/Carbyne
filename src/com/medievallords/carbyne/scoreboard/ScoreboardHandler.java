package com.medievallords.carbyne.scoreboard;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class ScoreboardHandler implements Listener {

    private Carbyne carbyne = Carbyne.getInstance();

    public HashMap<UUID, PlayerScoreboard> playerTasks = new HashMap<>();

//    public ScoreboardHandler() {
//        Bukkit.getPluginManager().registerEvents(this, carbyne);
//
//        for (Player all : PlayerUtility.getOnlinePlayers()) {
//            addTask(all);
//        }
//    }
//
//    public void addTask(Player player) {
//        playerTasks.put(player.getUniqueId(), new PlayerScoreboard(player, Bukkit.getScoreboardManager().getNewScoreboard()));
//    }
//
//    @EventHandler
//    public void onJoin(PlayerJoinEvent e) {
//        Player player = e.getPlayer();
//
//        if (!playerTasks.containsKey(player.getUniqueId())) {
//            addTask(player);
//        }
//    }
//
//    @EventHandler
//    public void onQuit(PlayerQuitEvent e) {
//        Player player = e.getPlayer();
//
//        if (playerTasks.containsKey(player.getUniqueId())) {
//            playerTasks.remove(player.getUniqueId());
//        }
//    }

    public HashMap<UUID, PlayerScoreboard> getPlayerTasks() {
        return playerTasks;
    }
}