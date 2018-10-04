package com.medievallords.carbyne.dungeons.dungeons.listeners;


import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.Dungeon;
import com.medievallords.carbyne.dungeons.dungeons.MobData;
import com.medievallords.carbyne.dungeons.dungeons.instance.DungeonInstance;
import com.medievallords.carbyne.dungeons.dungeons.instance.DungeonStage;
import com.medievallords.carbyne.dungeons.player.DPlayer;
import com.medievallords.carbyne.utils.*;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Created by WE on 2017-09-28.
 */

public class DungeonInstanceListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.PHYSICAL || action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR) {
            return;
        }

        Block block = event.getClickedBlock();

        Player player = event.getPlayer();

        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(player.getUniqueId());
        if (dPlayer == null) {
            return;
        }

        DungeonInstance instance = dPlayer.getInstance();

        Dungeon dungeon = instance.getDungeon();

        if (dungeon.getReadyLocation() != null) {
            Location ready = new Location(instance.getWorld(), dungeon.getReadyLocation().getX(), dungeon.getReadyLocation().getY(), dungeon.getReadyLocation().getZ());
            ready.setWorld(instance.getWorld());
            if (block.getLocation().equals(ready)) {
                instance.readyUp(dPlayer);
                return;
            }
        }

        if (dungeon.getExitLocation() != null) {
            Location exit = new Location(instance.getWorld(), dungeon.getExitLocation().getX(), dungeon.getExitLocation().getY(), dungeon.getExitLocation().getZ());
            exit.setWorld(instance.getWorld());
            if (block.getLocation().equals(exit)) {
                instance.onLeave(dPlayer);
            }
        }
    }

    @EventHandler
    public void onChest(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.CHEST) || e.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
                Block block = e.getClickedBlock();
                Location location = block.getLocation();

                DungeonInstance instance = StaticClasses.dungeonHandler.getInstance(location.getWorld());
                if (instance == null) {
                    return;
                }

                if (instance.getLootChests().contains(location)) {
                    Location clone = new Location(instance.getDungeon().getWorld(), location.getX(), location.getY(), location.getZ());
                    String item = instance.getDungeon().getLootChests().get(clone);

                    String itemName;
                    int amount = 1;

                    if (item == null) {
                        Bukkit.getLogger().log(Level.WARNING, "Could not find loot table for dungeon: " + instance.getDungeon().getName());
                        return;
                    }

                    if (item.contains(":")) {
                        String[] split = item.split(":");
                        itemName = split[0];
                        try {
                            amount = Integer.parseInt(split[1]);
                        } catch (NumberFormatException exception) {
                            Bukkit.getLogger().log(Level.WARNING, "Could not find loot table for dungeon: " + instance.getDungeon().getName());
                        }
                    } else {
                        itemName = item;
                    }

                    Chest chest = (Chest) block.getState();
                    Optional<MythicItem> mythicItem = MythicMobs.inst().getItemManager().getItem(itemName);
                    if (mythicItem.isPresent()) {
                        chest.getBlockInventory().addItem(BukkitAdapter.adapt(mythicItem.get().generateItemStack(amount)));
                    } else {
                        Material material = Material.getMaterial(itemName.toUpperCase());
                        if (Arrays.asList(Material.values()).contains(material)) {
                            chest.getBlockInventory().addItem(new ItemBuilder(material).amount(amount).build());
                        }
                    }

                    instance.getLootChests().remove(location);
                }
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().replace("/", "").split(" ");

        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(event.getPlayer().getUniqueId());
        if (dPlayer == null) {
            return;
        }

        List<String> commands = Carbyne.getInstance().getConfig().getStringList("dungeon-disabled-commands");

        if (commands.contains(args[0].toLowerCase()) && !event.getPlayer().hasPermission("carbyne.dungeon.bypass")) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou can not use this command whilst in a dungeon.");
        }
    }

    @EventHandler
    public void onMobSpawn(SpawnerSpawnEvent event) {
        DungeonInstance instance = StaticClasses.dungeonHandler.getInstance(event.getEntity().getWorld());
        if (instance == null) {
            return;
        }

        event.getEntity().remove();

        CreatureSpawner spawner = event.getSpawner();
        if (spawner == null) {
            return;
        }

        if (instance.getSpawners() != null && instance.getSpawners().containsKey(spawner.getLocation())) {
            MythicMob mob = instance.getSpawners().get(spawner.getLocation());
            instance.getSpawners().remove(spawner.getLocation());
            mob.spawn(BukkitAdapter.adapt(event.getLocation()), 1);
            spawner.getBlock().setType(Material.STONE);
            return;
        }

        MobData mobData = instance.getDungeon().getMobs().get(event.getEntity().getType());
        if (mobData == null) {
            MythicMob randomMob = instance.getDungeon().getRandomMob();
            if (randomMob != null) {
                randomMob.spawn(BukkitAdapter.adapt(event.getLocation()), 1);
            }

            return;
        }

        if (spawner.hasMetadata("mobCount")) {
            int count = spawner.getMetadata("mobCount").get(0).asInt();
            if (count + 1 >= mobData.getMaxAmount()) {
                spawner.getBlock().setType(Material.STONE);
            } else {
                event.getSpawner().setMetadata("mobCount", new FixedMetadataValue(Carbyne.getInstance(), count + 1));
            }
        } else {
            if (1 >= mobData.getMaxAmount()) {
                spawner.getBlock().setType(Material.STONE);
            } else {
                event.getSpawner().setMetadata("mobCount", new FixedMetadataValue(Carbyne.getInstance(), 1));
            }
        }

        MythicMob randomMob = instance.getDungeon().getRandomMob(mobData);
        if (randomMob != null) {
            randomMob.spawn(BukkitAdapter.adapt(event.getLocation()), 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        Cooldowns.setCooldown(player.getUniqueId(), "combat", 15000);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(event.getPlayer().getUniqueId());
        if (dPlayer == null) {
            return;
        }

        dPlayer.getInstance().onQuit(dPlayer);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(event.getPlayer().getUniqueId());
        if (dPlayer == null) {
            return;
        }

        dPlayer.getInstance().onJoin(dPlayer);
    }

    @EventHandler
    public void a(PlayerDeathEvent event) {
        Player player = event.getEntity();
        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(player.getUniqueId());
        if (dPlayer == null) {
            return;
        }

        DungeonInstance instance = dPlayer.getInstance();
        //Dungeon dungeon = instance.getDungeon();
        com.boydti.fawe.util.TaskManager.IMP.later(new Runnable() {
            @Override
            public void run() {
                instance.onDeath(dPlayer);
            }
        }, 1);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(player.getUniqueId());
        if (dPlayer == null) {
            return;
        }

        DungeonInstance instance = dPlayer.getInstance();
        instance.onRespawn(dPlayer);
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        if (!event.getEntity().getType().equals(EntityType.ZOMBIE)) {
            return;
        }

        DungeonInstance instance = StaticClasses.dungeonHandler.getInstance(event.getEntity().getWorld());
        if (instance == null) {
            return;
        }

        if (!event.getEntity().hasMetadata("fallenSoldier")) {
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(player.getUniqueId());
        if (dPlayer != null) {
            if (dPlayer.getInstance().getStage() == DungeonStage.PREPARING) {
                return;
            }

            if (!event.getFrom().getWorld().equals(event.getTo().getWorld()) && !event.getTo().getWorld().equals(dPlayer.getInstance().getWorld())) {
                if (player.hasPermission("carbyne.dungeon.bypass")) {
                    dPlayer.getInstance().onLeave(dPlayer);
                } else {
                    event.setCancelled(true);
                    MessageManager.sendMessage(player, "&cYou cannot teleport out of the dungeon. Use &7/dungeon leave");
                }
            }
        } else {
            DungeonInstance instance = StaticClasses.dungeonHandler.getInstance(event.getTo().getWorld());
            if (instance == null) {
                return;
            }

            if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
                return;
            }

            if (player.hasPermission("carbyne.dungeon.bypass")) {
                MessageManager.sendMessage(player, "&aYou have teleported to a dungeon world.");
            } else {
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&cYou cannot teleport in to a dungeon.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(event.getPlayer().getUniqueId());
        if (dPlayer != null && !event.getPlayer().hasPermission("carbyne.dungeon.bypass")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(event.getPlayer().getUniqueId());
        if (dPlayer != null && !event.getPlayer().hasPermission("carbyne.dungeon.bypass")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplosion(BlockExplodeEvent event) {
        DungeonInstance instance = StaticClasses.dungeonHandler.getInstance(event.getBlock().getWorld());
        if (instance != null) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        DungeonInstance instance = StaticClasses.dungeonHandler.getInstance(event.getEntity().getWorld());
        if (instance != null) {
            event.blockList().clear();
        }
    }
}
