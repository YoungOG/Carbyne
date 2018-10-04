package com.medievallords.carbyne.staff.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


public class PinListeners implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("carbyne.staff.pin")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());
                    StaticClasses.staffManager.getFrozenStaff().add(player.getUniqueId());
                    player.setWalkSpeed(0);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 1000));

                    if (!profile.hasPin()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {

                                if (!profile.hasPin()) {
                                    MessageManager.sendMessage(player, "&cPlease setup your four digit PIN. /setpin ####");
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Carbyne.getInstance(), 0, 5 * 20);
                    } else {
                        //Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

                        //Update vv
//                        if (!profile.getLastUsedIP().equalsIgnoreCase(profile.getCurrentIP())) {
//                            return;
//                        }
                        //Update ^^

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (StaticClasses.staffManager.getFrozenStaff().contains(player.getUniqueId())) {
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

//    @EventHandler
//    public void onMove(PlayerMoveEvent event) {
//        Player player = event.getPlayer();
//
//        if (player.hasPermission("carbyne.staff.pin")) {
//            Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());
//
//            if (!profile.hasPin() || StaticClasses.staffManager.getFrozenStaff().contains(player.getUniqueId())) {
//                event.setTo(event.getFrom());
//            }
//        }
//    }

//    @EventHandler
//    public void onExit(VehicleExitEvent event) {
//        if (!(event.getExited() instanceof Player)) {
//            return;
//        }
//
//        Player player = (Player) event.getExited();
//        if (player.hasPermission("carbyne.staff.pin")) {
//            if (StaticClasses.staffManager.getFrozenStaff().contains(player.getUniqueId())) {
//                event.setCancelled(true);
//            }
//        }
//    }

//    @EventHandler
//    public void onToggleSprint(PlayerToggleSprintEvent event) {
//        Player player = event.getPlayer();
//        if (player.hasPermission("carbyne.staff.pin") && event.isSprinting()) {
//            if (StaticClasses.staffManager.getFrozenStaff().contains(player.getUniqueId())) {
//                event.setCancelled(true);
//            }
//        }
//    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("carbyne.staff.pin")) {
            if (StaticClasses.staffManager.getFrozenStaff().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("carbyne.staff.pin")) {
            if (StaticClasses.staffManager.getFrozenStaff().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();

            if (player.hasPermission("carbyne.staff.pin")) {

                if (StaticClasses.staffManager.getFrozenStaff().contains(player.getUniqueId())) {
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
                if (StaticClasses.staffManager.getFrozenStaff().contains(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("carbyne.staff.pin")) {
            if (event.getMessage().toLowerCase().contains("/setpin")) {
                return;
            }

            Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

            if (!profile.hasPin()) {
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&7You cannot use commands until you have entered your PIN.");
            }

            if (StaticClasses.staffManager.getFrozenStaff().contains(player.getUniqueId())) {
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&7You cannot use commands until you have entered your PIN.");
            }
        }
    }
}
