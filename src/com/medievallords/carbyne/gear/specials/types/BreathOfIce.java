package com.medievallords.carbyne.gear.specials.types;

import com.boydti.fawe.util.TaskManager;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.PlayerHealth;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BreathOfIce implements Special, Listener {

    //private final ItemStack packedIce = new ItemStack(Material.PACKED_ICE);
    private List<UUID> uniqueIds = new ArrayList<>();

    public BreathOfIce() {
        Bukkit.getPluginManager().registerEvents(this, Carbyne.getInstance());
    }

    @Override
    public int getRequiredCharge() {
        return 100;
    }

    @Override
    public String getSpecialName() {
        return "BreathOfIce";
    }

    @Override
    public void callSpecial(Player caster) {
        new BukkitRunnable() {
            double t = 0;
            @Override
            public void run() {
                t++;

                //Vector original = caster.getLocation().getDirection().normalize();
                ItemStack ice = new ItemBuilder(Material.ICE).name("" + t).build();
                Vector vector = new Vector(ThreadLocalRandom.current().nextDouble(2) - 1, ThreadLocalRandom.current().nextDouble(0.3), ThreadLocalRandom.current().nextDouble(2) - 1);
                Item ice1 = caster.getWorld().dropItem(caster.getEyeLocation(), ice);
                ice1.setMetadata("player", new FixedMetadataValue(Carbyne.getInstance(), caster.getName()));
                uniqueIds.add(ice1.getUniqueId());
//                Item packed1 = caster.getWorld().dropItem(caster.getEyeLocation(), packedIce);
//                Item packed2 = caster.getWorld().dropItem(caster.getEyeLocation(), packedIce);
                TaskManager.IMP.later(new Runnable() {
                    @Override
                    public void run() {
                        ice1.remove();
//                        packed1.remove();
//                        packed2.remove();
                    }
                }, 100);

                ice1.setPickupDelay(1);
//                packed1.setPickupDelay(1000000);
//                packed2.setPickupDelay(1000000);

                ice1.setVelocity(vector);
//                packed1.setVelocity(vector);
//                packed2.setVelocity(vector);

                if (t > 70) {
                    cancel();
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 2);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (uniqueIds.contains(event.getItem().getUniqueId())) {
            uniqueIds.remove(event.getItem().getUniqueId());
            event.setCancelled(true);
            if (event.getItem().getMetadata("player").get(0).asString().equalsIgnoreCase(event.getPlayer().getName())) {
                return;
            }

            event.getItem().setPickupDelay(100000);
            PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(event.getPlayer().getUniqueId());
            playerHealth.setHealth(playerHealth.getHealth() - 100);
            event.getPlayer().damage(0.0);
        }
    }
}
