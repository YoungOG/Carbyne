package com.medievallords.carbyne.staff.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class FreezeListeners implements Listener {

    private StaffManager staffManager = Carbyne.getInstance().getStaffManager();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getFrom().getY() != event.getTo().getY()) {
            if (staffManager.getFrozen().contains(player.getUniqueId())) {
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (staffManager.getFrozen().contains(player.getUniqueId())) {
            staffManager.unfreezePlayer(player);
            MessageManager.broadcastMessage("&7[&c!&7] &5" + player.getName() + " &chas logged out while frozen!", "carbyne.command.freeze");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (staffManager.getFrozen().contains(event.getEntity().getUniqueId())) {
                event.setCancelled(true);

                if (event.getDamager() instanceof Player) {
                    MessageManager.sendMessage(event.getDamager(), "&cYou cannot damage frozen players.");
                }
            }
        }

        if (event.getDamager() instanceof Player) {
            if (staffManager.getFrozen().contains(event.getDamager().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (staffManager.getFrozen().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (staffManager.getFrozen().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot build while frozen.");
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (staffManager.getFrozen().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot build while frozen.");
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (staffManager.getFrozen().contains(event.getPlayer().getUniqueId())) {
            String message = event.getMessage();

            if (!message.startsWith("/message") && !message.startsWith("/r") && !message.startsWith("/msg") && !message.startsWith("/reply")) {
                event.setCancelled(true);
                MessageManager.sendMessage(event.getPlayer(), "&cYou cannot use this command while frozen.");
            }
        }
    }
}
