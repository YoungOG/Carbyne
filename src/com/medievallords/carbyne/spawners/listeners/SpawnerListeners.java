package com.medievallords.carbyne.spawners.listeners;

import com.medievallords.carbyne.spawners.CreateSpawners;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Williams on 2017-03-17.
 * for the Carbyne project.
 */
public class SpawnerListeners implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("carbyne.spawners.administrator")) {
            if (event.getPlayer().getInventory().getItemInHand().hasItemMeta() && event.getPlayer().getInventory().getItemInHand().getItemMeta().hasDisplayName()) {
                if (event.getPlayer().getInventory().getItemInHand().getType() == Material.GOLD_AXE && event.getPlayer().getInventory().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&5&l&nWand"))) {
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        event.setCancelled(true);
                        CreateSpawners.getPos1().put(event.getPlayer(), event.getClickedBlock().getLocation());
                        MessageManager.sendMessage(event.getPlayer(), "&ePosition 1 set.");
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        event.setCancelled(true);
                        CreateSpawners.getPos2().put(event.getPlayer(), event.getClickedBlock().getLocation());
                        MessageManager.sendMessage(event.getPlayer(), "&ePosition 2 set.");
                    }
                }
            }
        }
    }
}
