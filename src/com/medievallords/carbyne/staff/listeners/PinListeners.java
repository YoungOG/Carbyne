package com.medievallords.carbyne.staff.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class PinListeners implements Listener {

    private ProfileManager profileManager = Carbyne.getInstance().getProfileManager();
    private StaffManager staffManager = Carbyne.getInstance().getStaffManager();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("carbyne.staff.pin")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!profileManager.getProfile(player.getUniqueId()).hasPin()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Profile profile = profileManager.getProfile(player.getUniqueId());

                                if (!profile.hasPin()) {
                                    MessageManager.sendMessage(player, "&cPlease setup your four digit PIN. /setpin ####");
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Carbyne.getInstance(), 0, 5 * 20);
                    } else {
                        Profile profile = profileManager.getProfile(player.getUniqueId());

                        //Update vv
//                        if (!profile.getLastUsedIP().equalsIgnoreCase(profile.getCurrentIP())) {
//                            return;
//                        }
                        //Update ^^

                        staffManager.getFrozenStaff().add(player.getUniqueId());

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (staffManager.getFrozenStaff().contains(player.getUniqueId())) {
                                    MessageManager.sendMessage(player, "&7Please enter your PIN.");
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Carbyne.getInstance(), 0, 5 * 20);
                    }
                }
            }.runTaskLater(Carbyne.getInstance(), 5L);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("carbyne.staff.pin")) {
            Profile profile = profileManager.getProfile(player.getUniqueId());

            if (!profile.hasPin() || staffManager.getFrozenStaff().contains(player.getUniqueId())) {
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("carbyne.staff.pin")) {
            Profile profile = profileManager.getProfile(player.getUniqueId());

            if (!profile.hasPin() || staffManager.getFrozenStaff().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();

            if (player.hasPermission("carbyne.staff.pin")) {
                Profile profile = profileManager.getProfile(player.getUniqueId());

                if (!profile.hasPin() || staffManager.getFrozenStaff().contains(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamage2(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (player.hasPermission("carbyne.staff.pin")) {
                Profile profile = profileManager.getProfile(player.getUniqueId());

                if (!profile.hasPin() || staffManager.getFrozenStaff().contains(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("carbyne.staff.pin")) {
            if (!event.getMessage().toLowerCase().contains("/setpin")) { //It's like this line is ignored xD
                Profile profile = profileManager.getProfile(player.getUniqueId());

                if (!profile.hasPin()) {
                    event.setCancelled(true);
                    MessageManager.sendMessage(player, "&7You cannot use commands until you have entered your PIN.");
                }
            }

            if (staffManager.getFrozenStaff().contains(player.getUniqueId())) {
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&7You cannot use commands until you have entered your PIN.");
            }
        }
    }
}
