package com.medievallords.carbyne.regeneration;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by Calvin on 3/24/2017
 * for the Carbyne project.
 */
public class RegenerationListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();
    private RegenerationHandler regenerationHandler = main.getRegenerationHandler();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getWorld().getName().equalsIgnoreCase("player_world")) {
            regenerationHandler.request(block, BlockRegenerationType.BROKEN);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getWorld().getName().equalsIgnoreCase("player_world")) {
            regenerationHandler.request(block, BlockRegenerationType.PLACED);
        }
    }
}
