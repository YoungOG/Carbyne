package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.WorldCoord;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.List;

public class DamageListener implements Listener {

    private Carbyne main = Carbyne.getInstance();
    private GearManager gearManager = main.getGearManager();

    private ArrayList<String> warzoneCmds = new ArrayList<>(), safezoneCmds = new ArrayList<>();

    public DamageListener() {
        if (main.getConfig().getStringList("blocked-cmds.warzone").size() > 0) {
            warzoneCmds.addAll(main.getConfig().getStringList("blocked-cmds.warzone"));
        }

        if (main.getConfig().getStringList("blocked-cmds.safezone").size() > 0) {
            safezoneCmds.addAll(main.getConfig().getStringList("blocked-cmds.safezone"));
        }
    }

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

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        WorldCoord worldCoord = WorldCoord.parseWorldCoord(player);

        try {
            if (worldCoord.getTownBlock() != null && worldCoord.getTownBlock().hasTown()) {
                if (worldCoord.getTownBlock().getTown().getName().equalsIgnoreCase("Warzone")) {
                    for (String str : warzoneCmds) {
                        if (command.startsWith(str)) {
                            event.setCancelled(true);
                            MessageManager.sendMessage(player, "&cYou cannot use this command in warzones.");
                        }
                    }
                } else if (worldCoord.getTownBlock().getTown().getName().equalsIgnoreCase("Safezone")) {
                    for (String str : safezoneCmds) {
                        if (command.startsWith(str)) {
                            event.setCancelled(true);
                            MessageManager.sendMessage(player, "&cYou cannot use this command in safezones.");
                        }
                    }
                }
            }
        } catch (NotRegisteredException ignored) {}
    }
}
