package com.medievallords.carbyne.listeners;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    private static final List<BlockFace> ALL_DIRECTIONS = ImmutableList.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private final Map<UUID, Set<Location>> previousUpdates = new HashMap<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("ForceField Thread").build());

    @EventHandler
    public void shutdown(PluginDisableEvent event) {
        // Do nothing if plugin being disabled isn't CombatTagPlus
        if (event.getPlugin() != main)
            return;

        // Shutdown executor service and clean up threads
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignore) {}

        // Go through all previous updates and revert spoofed blocks
        for (UUID uuid : previousUpdates.keySet()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null)
                continue;

            for (Location location : previousUpdates.get(uuid)) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getType(), block.getData());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateViewedBlocks(PlayerMoveEvent event) {
        // Do nothing if check is not active
        if (!main.isCombatTagPlusEnabled())
            return;

        // Do nothing if player hasn't moved over a whole block
        Location t = event.getTo();
        Location f = event.getFrom();

        if (t.getBlockX() == f.getBlockX() && t.getBlockY() == f.getBlockY() && t.getBlockZ() == f.getBlockZ()) {
            return;
        }

        final Player player = event.getPlayer();

        // Asynchronously send block changes around player
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                // Stop processing if player has logged off
                UUID uuid = player.getUniqueId();

                if (!main.getCombatTagPlus().getPlayerCache().isOnline(uuid)) {
                    previousUpdates.remove(uuid);
                    return;
                }

                // Update the players force field perspective and find all blocks to stop spoofing
                Set<Location> changedBlocks = getChangedBlocks(player);
                Material forceFieldMaterial = Material.STAINED_GLASS;
                byte forceFieldMaterialDamage = 14;

                Set<Location> removeBlocks;
                if (previousUpdates.containsKey(uuid)) {
                    removeBlocks = previousUpdates.get(uuid);
                } else {
                    removeBlocks = new HashSet<>();
                }

                for (Location location : changedBlocks) {
                    player.sendBlockChange(location, forceFieldMaterial, forceFieldMaterialDamage);
                    removeBlocks.remove(location);
                }

                // Remove no longer used spoofed blocks
                for (Location location : removeBlocks) {
                    Block block = location.getBlock();
                    player.sendBlockChange(location, block.getType(), block.getData());
                }

                previousUpdates.put(uuid, changedBlocks);
            }
        });
    }

    private Set<Location> getChangedBlocks(Player player) {
        Set<Location> locations = new HashSet<>();

        // Do nothing if player is not tagged
        if (!main.combatTagPlus.getTagManager().isTagged(player.getUniqueId()))
            return locations;

        // Find the radius around the player
        int r = 10;
        Location l = player.getLocation();
        Location loc1 = l.clone().add(r, 0, r);
        Location loc2 = l.clone().subtract(r, 0, r);
        int topBlockX = loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
        int bottomBlockX = loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
        int topBlockZ = loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();
        int bottomBlockZ = loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();

        // Iterate through all blocks surrounding the player
        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                // Location corresponding to current loop
                Location location = new Location(l.getWorld(), (double) x, l.getY(), (double) z);

                // PvP is enabled here, no need to do anything else
                if (isPvpEnabledAt(location)) continue;

                // Check if PvP is enabled in a location surrounding this
                if (!isPvpSurrounding(location)) continue;

                for (int i = -r; i < r; i++) {
                    Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());

                    loc.setY(loc.getY() + i);

                    // Do nothing if the block at the location is not air
                    if (!loc.getBlock().getType().equals(Material.AIR)) continue;

                    // Add this location to locations
                    locations.add(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                }
            }
        }

        return locations;
    }

    private boolean isPvpSurrounding(Location loc) {
        for (BlockFace direction : ALL_DIRECTIONS) {
            if (isPvpEnabledAt(loc.getBlock().getRelative(direction).getLocation())) {
                return true;
            }
        }

        return false;
    }

    public boolean isPvpEnabledAt(Location loc) {
        TownyWorld world;
        try {
            world = TownyUniverse.getDataSource().getWorld(loc.getWorld().getName());
        } catch (NotRegisteredException ignore) {
            return true;
        }

        TownBlock townBlock = null;
        try {
            townBlock = world.getTownBlock(Coord.parseCoord(loc));
        } catch (NotRegisteredException ignore) {

        }

        return !CombatUtil.preventPvP(world, townBlock);
    }


}
