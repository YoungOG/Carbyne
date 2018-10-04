package com.medievallords.carbyne.staff.listeners;

import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StaffListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void a(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("carbyne.staff") && !StaticClasses.staffManager.getVanish().contains(event.getPlayer().getUniqueId())) {
            StaticClasses.staffManager.getStaff().add(event.getPlayer().getUniqueId());

        }
    }

    @EventHandler
    public void b(PlayerQuitEvent event) {
        if (event.getPlayer().hasPermission("carbyne.staff")) {
            StaticClasses.staffManager.getStaff().remove(event.getPlayer().getUniqueId());
        }
    }
}
