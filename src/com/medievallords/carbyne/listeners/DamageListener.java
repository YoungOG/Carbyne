package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.MessageManager;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.WorldCoord;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicReloadedEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
                    CarbyneGear replacement = gearManager.getCarbyneGear(ChatColor.stripColor(item.getItemMeta().getDisplayName()));

                    String[] gear = ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(",");
                    if (gear.length > 1) {
                        int random = new Random().nextInt(gear.length);
                        CarbyneGear carbyneGear = gearManager.getCarbyneGear(gear[random]);
                        if (carbyneGear != null) {
                            e.getDrops().remove(item);
                            e.getDrops().add(carbyneGear.getItem(false));
                        }
                    }

                    if (replacement != null) {
                        e.getDrops().remove(item);
                        e.getDrops().add(replacement.getItem(false));
                    } else if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).contains("randomGear")) {
                        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        String[] split = name.split(":");
                        if (split.length > 1) {
                            replacement = gearManager.getRandomCarbyneGear(Boolean.parseBoolean(split[1]));
                            e.getDrops().remove(item);
                            e.getDrops().add(replacement.getItem(false));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            WorldCoord worldCoord = WorldCoord.parseWorldCoord(player);

            try {
                if (worldCoord.getTownBlock() != null && worldCoord.getTownBlock().hasTown()) {
                    if (worldCoord.getTownBlock().getTown().getName().equalsIgnoreCase("Safezone") && !CombatTagListeners.isInCombat(player.getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
            } catch (NotRegisteredException ignored) {
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
                        if (command.startsWith(str) && !player.hasPermission("carbyne.staff")) {
                            event.setCancelled(true);
                            MessageManager.sendMessage(player, "&cYou cannot use this command in warzones.");
                        }
                    }
                } else if (worldCoord.getTownBlock().getTown().getName().equalsIgnoreCase("Safezone")) {
                    for (String str : safezoneCmds) {
                        if (command.startsWith(str) && !player.hasPermission("carbyne.staff")) {
                            event.setCancelled(true);
                            MessageManager.sendMessage(player, "&cYou cannot use this command in safezones.");
                        }
                    }
                }
            }
        } catch (NotRegisteredException ignored) {
        }
    }

    @EventHandler
    public void onMythicMobDisable(MythicReloadedEvent event) {
        for (ActiveMob am : event.getInstance().getMobManager().getActiveMobs()) {
            am.setDead();
        }

        for (MythicSpawner ms : event.getInstance().getSpawnerManager().getSpawners()) {
            ms.resetTimers();
            ms.getAssociatedMobs().clear();
            ms.setOnWarmup();
            ms.setRemainingWarmupSeconds(30);
            if (ms.getWarmupSeconds() > 1000) {
                ms.setActivationRange(50);
            } else {
                ms.setActivationRange(25);
            }
        }
    }

    /*@EventHandler
    public void onChunkLoad (ChunkLoadEvent event) {
        for (MythicSpawner ms : MythicMobs.inst().getSpawnerManager().getSpawners()) {
            if (ms.getLocation().getChunkX() == event.getChunk().getX() && ms.getLocation().getChunkZ() == event.getChunk().getZ() && ms.getWarmupSeconds() > 1000) {
                try {
                    Field field = ms.getClass().getDeclaredField("ICD");
                    field.setAccessible(true);
                    field.set(ms.getInternalCooldown(), 1);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    /*@EventHandler
    public void onDespawn(Spawner spawner)
    {

    }*/

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageIndicator(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity livingEntity = (LivingEntity) event.getEntity();

        if (livingEntity.getNoDamageTicks() > livingEntity.getMaximumNoDamageTicks() / 2.0f) {
            return;
        }

        if (event.getDamage() <= 0.0) {
            return;
        }

        if (main.getConfig().getBoolean("Damage-Indicators.Calculate-Armor")) {
            double previousHealth = (livingEntity).getHealth();

            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                double actualDamage = livingEntity.isDead() ? previousHealth : (previousHealth - (livingEntity).getHealth());
                if (actualDamage <= 0.0) {
                    return;
                }

                showDamageIndicator(livingEntity, actualDamage);
            });
        } else {
            showDamageIndicator(livingEntity, event.getDamage());
        }
    }

    private void showDamageIndicator(final LivingEntity entity, final double damage) {
        main.getPacketManager().sendDamageIndicator(entity, entity.getEyeLocation().add(0.0, 0.6, 0.0), MessageManager.replaceSymbols(main.getConfig().getString("Damage-Indicators.Format").replace("{damage}", "%d").replace("%d", formatDamage(damage))), main.getConfig().getBoolean("Damage-Indicators.Animate"), main.getConfig().getInt("Damage-Indicators.Hide-After-Ticks"));
    }

    public String formatDamage(double damage) {
        if (main.getConfig().getBoolean("Damage-Indicators.Decimal-Places")) {
            return new DecimalFormat("0.0").format(damage).replace(",", ".");
        }
        return Integer.toString(roundDownMinimumOne(damage));
    }

    private int roundDownMinimumOne(double d) {
        int round = (int) d;
        return (round < 1) ? 1 : round;
    }
}
