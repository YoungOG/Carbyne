package com.medievallords.carbyne.staff.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.staff.StaffManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StaffListeners implements Listener {

    private ProfileManager profileManager = Carbyne.getInstance().getProfileManager();
    private StaffManager staffManager = Carbyne.getInstance().getStaffManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void a(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("carbyne.staff") && !staffManager.getVanish().contains(event.getPlayer().getUniqueId())) {
            staffManager.getStaff().add(event.getPlayer().getUniqueId());

        }
    }

    @EventHandler
    public void b(PlayerQuitEvent event) {
        if (event.getPlayer().hasPermission("carbyne.staff")) {
            staffManager.getStaff().remove(event.getPlayer().getUniqueId());
        }
    }
}
