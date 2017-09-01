package com.medievallords.carbyne.packages.listeners;

import com.medievallords.carbyne.packages.Package;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by WE on 2017-08-04.
 */
public class PackageListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        for (Package pack : Package.packages) {
            if (event.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', pack.getDisplayName()))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() == Action.PHYSICAL || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() != Material.AIR && event.getPlayer().getItemInHand().hasItemMeta() && event.getPlayer().getItemInHand().getItemMeta().hasLore()) {

            if (Package.getPackage(event.getPlayer().getItemInHand()) != null) {
                event.setCancelled(true);
                MessageManager.sendMessage(event.getPlayer(), "&cYou can not do this while holding a package");
            }
        }
    }
}
