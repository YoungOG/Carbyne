package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;

public class DamageListener implements Listener {

    private Carbyne main = Carbyne.getInstance();
    private GearManager gearManager = main.getGearManager();

    @EventHandler
    public void onMobDeath(MythicMobDeathEvent e) {
        if (e.getDrops().size() <= 0) {
            return;
        }

        List<ItemStack> drops = e.getDrops();

        for (ItemStack item : drops) {
            if (item != null && item.getType() == Material.QUARTZ) {
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    ItemStack replacement = gearManager.getCarbyneGear(ChatColor.stripColor(item.getItemMeta().getDisplayName())).getItem(false);

                    if (replacement != null) {
                        e.getDrops().remove(item);
                        e.getDrops().add(replacement);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getDamager() instanceof Player) {
                try {
                    Resident damager = PlayerUtility.getResident((Player) e.getDamager());
                    Resident attacked = PlayerUtility.getResident((Player) e.getEntity());

                    if (damager != null && attacked != null && damager.hasTown() && attacked.hasTown()) {
                        Town defender = attacked.getTown();
                        Town attacker = damager.getTown();

                        if (attacker.equals(defender)) {

                            e.setCancelled(true);
                            return;
                        }

                        if (defender.getNation().equals(attacker.getNation())) {
                            e.setCancelled(true);
                        }
                    }
                } catch (NotRegisteredException ignored) {
                }
            } else if (e.getDamager() instanceof Projectile) {
                ProjectileSource p = ((Projectile) e.getDamager()).getShooter();

                if (p instanceof Player) {
                    try {
                        Resident damager = PlayerUtility.getResident((Player) p);
                        Resident attacked = PlayerUtility.getResident((Player) e.getEntity());

                        if (damager != null && attacked != null && damager.hasTown() && attacked.hasTown()) {
                            Town defender = attacked.getTown();
                            Town attacker = damager.getTown();

                            if (attacker.equals(defender)) {
                                e.setCancelled(true);
                                return;
                            }

                            if (defender.getNation().equals(attacker.getNation())) {
                                e.setCancelled(true);
                            }
                        }
                    } catch (NotRegisteredException ignored) {
                    }
                }
            }
        }
    }
}
