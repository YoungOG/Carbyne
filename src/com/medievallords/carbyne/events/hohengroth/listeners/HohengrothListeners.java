package com.medievallords.carbyne.events.hohengroth.listeners;

import com.medievallords.carbyne.events.hohengroth.HohengrothEvent;
import com.medievallords.carbyne.utils.MessageManager;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by William on 7/10/2017.
 */
public class HohengrothListeners implements Listener {

    private HohengrothEvent hohengrothEvent;

    public HohengrothListeners(HohengrothEvent hohengrothEvent) {
        this.hohengrothEvent = hohengrothEvent;
    }

    @EventHandler
    public void onMobDeath(MythicMobDeathEvent event) {
        if (event.getMob().getType().getInternalName().equals("Hohengroth") && (hohengrothEvent.getHohengrothEntity() != null && event.getEntity().getUniqueId().equals(hohengrothEvent.getHohengrothEntity().getUniqueId()))) {
            //HOHENGROTH IS DEAD, EVENT IS OVER
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {

    }

    @EventHandler
    public void onEnter(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block.getLocation().equals(hohengrothEvent.getEnterEventLocation())) {
            if (hohengrothEvent.isActive() && hohengrothEvent.isOpen()) {
                event.setCancelled(true);
                hohengrothEvent.enterPlayer(event.getPlayer());
            } else {
                MessageManager.sendMessage(event.getPlayer(), "&cYou cannot enter at this time");
            }
        } else if (block.getLocation().equals(hohengrothEvent.getEnterBansheeLocation())) {
            hohengrothEvent.enterBansheePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            if (hohengrothEvent.getParticipants().contains(damager)) {

            }
        }
    }
}
