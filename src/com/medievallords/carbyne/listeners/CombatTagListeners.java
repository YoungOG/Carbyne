package com.medievallords.carbyne.listeners;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Calvin on 3/22/2017
 * for the Carbyne project.
 */
public class CombatTagListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();
    private static ConcurrentHashMap<UUID, Long> tagged = new ConcurrentHashMap<>();
    private static final List<BlockFace> ALL_DIRECTIONS = ImmutableList.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private final Map<UUID, Set<Location>> previousUpdates = new HashMap<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("ForceField Thread").build());

    @EventHandler
    public void onEnderchestOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.hasBlock() && event.getClickedBlock().getType() == Material.ENDER_CHEST && isInCombat(player.getUniqueId())) {
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&7You cannot open your enderchest during combat.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (damager instanceof Projectile) {
            if (((Projectile) damager).getShooter() != null) {
                damager = (Entity) ((Projectile) damager).getShooter();
            }
        }

        if (event.getEntity() instanceof Player) {
            Player tagged = (Player) event.getEntity();

            if (damager instanceof Player) {
                Player tagger = (Player) damager;

                if (tagger != tagged) {
                    setCombatTag(tagger, 45);
                    setCombatTag(tagged, 45);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        removeCombatTag(event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.isDead() || player.getHealth() <= 0) {
            removeCombatTag(player.getUniqueId());
            return;
        }

        if (isInCombat(player.getUniqueId())) {
            player.setHealth(0.0);
            removeCombatTag(player.getUniqueId());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        removeCombatTag(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (isInCombat(player.getUniqueId())) {
            String command = event.getMessage();

            for (String disabledCommand : main.getConfig().getStringList("combat-tag.blocked-cmds")) {
                if (command.indexOf(" ") == disabledCommand.length()) {
                    if (command.substring(0, command.indexOf(" ")).equalsIgnoreCase(disabledCommand)) {
                        MessageManager.sendMessage(player, "&7This command is disabled while in combat.");
                        event.setCancelled(true);
                        return;
                    }
                } else if (disabledCommand.indexOf(" ") > 0) {
                    if (command.toLowerCase().startsWith(disabledCommand.toLowerCase())) {
                        MessageManager.sendMessage(player, "&7This command is disabled while in combat.");
                        event.setCancelled(true);
                        return;
                    }
                } else if (!command.contains(" ") && command.equalsIgnoreCase(disabledCommand)) {
                    MessageManager.sendMessage(player, "&7This command is disabled while in combat.");
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void shutdown(PluginDisableEvent event) {
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
        // Do nothing if player hasn't moved over a whole block
        Location t = event.getTo();
        Location f = event.getFrom();

        if (t.getBlockX() == f.getBlockX() && t.getBlockY() == f.getBlockY() && t.getBlockZ() == f.getBlockZ()) {
            return;
        }

        final Player player = event.getPlayer();

        // Asynchronously send block changes around player
        executorService.submit(() -> {
            // Stop processing if player has logged off
            UUID uuid = player.getUniqueId();

            if (Bukkit.getPlayer(uuid) == null) {
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
        });
    }

    private Set<Location> getChangedBlocks(Player player) {
        Set<Location> locations = new HashSet<>();

        // Do nothing if player is not tagged
        if (!isInCombat(player.getUniqueId()))
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

    public static long getRemainingTime(UUID id) {
        Long tag = tagged.get(id);

        if (tag == null) {
            return -1;
        }

        return tag - System.currentTimeMillis();
    }

    public boolean setCombatTag(Player p, int seconds) {
        if (p.isOnline()) {
            tagged.put(p.getUniqueId(), PvPTimeout(seconds));

            Board board = Board.getByPlayer(p);
            if (board != null) {
                BoardCooldown boardCooldown = board.getCooldown("combattag");

                if (boardCooldown == null) {
                    new BoardCooldown(board, "combattag", main.getConfig().getInt("combat-tag.tag-length"));
                }
            }
            return true;
        }

        return false;
    }

    public long removeCombatTag(UUID id) {
        if (isInCombat(id)) {
            if (Bukkit.getPlayer(id) != null) {
                Player player = Bukkit.getPlayer(id);
                Board board = Board.getByPlayer(player);
                if (board != null) {
                    BoardCooldown boardCooldown = board.getCooldown("combattag");

                    if (boardCooldown != null) {
                        board.getCooldown("combattag").cancel();
                    }
                }
            }

            return tagged.remove(id);
        }

        return -1;
    }

    public long PvPTimeout(int seconds) {
        return System.currentTimeMillis() + (seconds * 1000);
    }

    public static boolean isInCombat(UUID id) {
        if (getRemainingTime(id) < 0) {
            if (tagged.containsKey(id)) {
                tagged.remove(id);
            }
            return false;
        } else {
            return true;
        }
    }

    public static String formatTime(long difference) {
        Calendar call = Calendar.getInstance();
        call.setTimeInMillis(difference);
        return new SimpleDateFormat("ss.S").format(difference);
    }

    public ImmutableSet<UUID> listTagged() {
        return ImmutableSet.copyOf(tagged.keySet());
    }
}
