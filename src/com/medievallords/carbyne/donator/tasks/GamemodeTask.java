package com.medievallords.carbyne.donator.tasks;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.donator.GamemodeManager;
import com.medievallords.carbyne.donator.listeners.GameModeListener;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Created by Dalton on 6/13/2017.
 */
public class GamemodeTask extends BukkitRunnable {

    private Carbyne main = Carbyne.getInstance();

    public static HashMap<Player, GamemodeTask> hasActiveTasks = new HashMap<>();

    private GamemodeManager gamemodeManager;
    private GameModeListener gameModeListener;
    private Player player;
    private int times = 4;

    public GamemodeTask(GamemodeManager gamemodeManager, GameModeListener gameModeListener, Player player) {
        this.gamemodeManager = gamemodeManager;
        this.gameModeListener = gameModeListener;
        this.player = player;
    }

    public void run() {
        if (gameModeListener.getTaskCheck().get(player)) {
            GamemodeTask task = hasActiveTasks.get(player);
            hasActiveTasks.remove(player);
            task.cancel();
            return;
        }

        if (!gamemodeManager.getFlyPlayers().contains(player) && !gamemodeManager.getGmPlayers().contains(player)) {
            GamemodeTask task = hasActiveTasks.get(player);
            hasActiveTasks.remove(player);
            task.cancel();
            return;
        }

        if (times <= 0) {
            new BukkitRunnable() {
                public void run() {
                    if (gamemodeManager.getFlyPlayers().contains(player)) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        gamemodeManager.getFlyPlayers().remove(player);
                        MessageManager.sendMessage(player, "&cFlight disabled!");
                    }
                    if (gamemodeManager.getGmPlayers().contains(player)) {
                        player.setGameMode(GameMode.SURVIVAL);
                        gamemodeManager.getGmPlayers().remove(player);
                        MessageManager.sendMessage(player, "&cCreative disabled!");
                    }
                }
            }.runTask(main);
            GamemodeTask task = hasActiveTasks.get(player);
            hasActiveTasks.remove(player);
            task.cancel();
        } else {
            MessageManager.sendMessage(player, "&c" + String.valueOf(times) + "...");
            times--;
        }
    }
}