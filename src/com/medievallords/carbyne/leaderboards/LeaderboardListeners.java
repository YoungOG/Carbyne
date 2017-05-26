package com.medievallords.carbyne.leaderboards;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Calvin on 5/15/2017
 * for the Carbyne project.
 */
public class LeaderboardListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();
    private LeaderboardManager leaderboardManager = main.getLeaderboardManager();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) event.getClickedBlock().getState();

                if (leaderboardManager.getLeaderboard(sign.getLocation()) != null) {
                    if (main.getProfileManager().getProfile(sign.getLine(2)) != null) {
                        player.performCommand("stats " + sign.getLine(2));
                    }
                }
            }
        }
    }
}
