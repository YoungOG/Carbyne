package com.medievallords.carbyne.economy;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by WE on 2017-08-06.
 */
public class MarketGuiManager {


    public void giveMarketVillager(Player player) {

    }

    public void placeMarketVillager(Player player, Location location) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setAdult();
        villager.setCustomName(ChatColor.translateAlternateColorCodes('&', player.getName()) + "'s Shop");
        villager.setCustomNameVisible(true);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 7, false, false));
        villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, false, false));
        villager.setCanPickupItems(false);
        villager.setMetadata("shopplayer", new FixedMetadataValue(Carbyne.getInstance(), player.getUniqueId().toString()));

    }

    public void checkVillagers() {
        new BukkitRunnable() {
            @Override
            public void run() {

            }
        }.runTaskTimer(Carbyne.getInstance(), 100, 10);
    }
}
