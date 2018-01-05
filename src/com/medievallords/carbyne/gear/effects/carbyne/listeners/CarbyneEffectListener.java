package com.medievallords.carbyne.gear.effects.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class CarbyneEffectListener implements Listener {

    @EventHandler
    public void onBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        ItemStack bow = event.getBow();
        if (bow == null) {
            return;
        }

        CarbyneWeapon carbyneWeapon = Carbyne.getInstance().getGearManager().getCarbyneWeapon(bow);
        if (carbyneWeapon == null) {
            return;
        }

        Player player = (Player) event.getEntity();


    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

    }

}
