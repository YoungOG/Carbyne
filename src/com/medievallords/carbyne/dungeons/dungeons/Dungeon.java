package com.medievallords.carbyne.dungeons.dungeons;

import com.boydti.fawe.util.TaskManager;
import com.destroystokyo.paper.Title;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.instance.DungeonInstance;
import com.medievallords.carbyne.dungeons.dungeons.options.SpawnMode;
import com.medievallords.carbyne.dungeons.triggers.Trigger;
import com.medievallords.carbyne.utils.*;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by WE on 2017-09-27.
 */

@Getter
@Setter
public class Dungeon {

    //private List<DungeonArena> dungeonArenas = new ArrayList<>();
    private String name;
    private World world;
    private Location spawnLocation, readyLocation, completeLocation, lobbyLocation, exitLocation;
    private long cooldown;
    private int minJoin;
    private boolean pvp = false, spellsAllowed;
    private SpawnMode spawnMode;

    private HashMap<Location, MythicMob> spawners = new HashMap<>();
    private HashMap<Location, String> lootChests = new HashMap<>();
    private HashMap<Location, Player> joinLocations = new HashMap<>();
    private HashMap<EntityType, MobData> mobs = new HashMap<>();

    private List<Trigger> triggers = new ArrayList<>();

    public Dungeon(String name, DungeonLineConfig lc) {
        this.name = name;
    }

    public void startDungeon(List<Player> players) {
        if (world == null) {
            Bukkit.getLogger().log(Level.WARNING, "DUNGEON " + name + " DOES NOT HAVE A TEMPLATE WORLD");
            return;
        }

        if (StaticClasses.dungeonHandler.getInstances().size() >= DungeonHandler.MAX_INSTANCES) {
            players.forEach(player -> MessageManager.sendMessage(player, "&cYou cannot start another dungeon."));
            return;
        }

        if (spawnLocation == null || readyLocation == null || lobbyLocation == null) {
            Bukkit.getLogger().log(Level.WARNING, "DUNGEON " + name + " DOES NOT HAVE ANY SPAWN POINTS");
            return;
        }

        DungeonHandler.INSTANCES++;
        int id = DungeonHandler.INSTANCES;

        TaskManager.IMP.async(new Runnable() {
            @Override
            public void run() {
                World newWorld = WorldLoader.createWorld(world, id);
                if (newWorld == null)
                    return;

                HashMap<Location, MythicMob> clonedSpawners = new HashMap<>();
                for (Location location : spawners.keySet()) {
                    Location cloned = new Location(newWorld, location.getX(), location.getY(), location.getZ());
                    clonedSpawners.put(cloned, spawners.get(location));
                }

                List<Location> lootChestsLocation = new ArrayList<>();
                for (Location location : lootChests.keySet()) {
                    Location copied = new Location(newWorld, location.getX(), location.getY(), location.getZ());
                    lootChestsLocation.add(copied);
                }

                DungeonInstance instance = new DungeonInstance(id, newWorld, Dungeon.this, players, triggers, clonedSpawners, lootChestsLocation);
                StaticClasses.dungeonHandler.getInstances().add(instance);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        instance.prepare();
                    }
                }.runTask(Carbyne.getInstance());
            }
        });
    }

    public void addPlayer(Player player, Location location) {
        joinLocations.put(location, player);
    }

    public void removePlayer(Location location) {
        Player player = joinLocations.get(location);
        if (player != null)
            player.sendTitle(new Title.Builder().title("").subtitle(ChatColor.translateAlternateColorCodes('&', "&cCancelled")).stay(7).build());

        joinLocations.put(location, null);
    }

    public void check() {
        int size = size();

        if (size >= minJoin) {
            new BukkitRunnable() {
                private int countdown = 10;

                @Override
                public void run() {
                    if (size() < minJoin) {
                        cancel();
                        sendAllTitle("&cEntering the dungeon", "&cCancelled");
                        return;
                    }

                    if (countdown <= 0) {
                        sendAllTitle("", "&a&lTeleporting...");
                        List<Player> players = new ArrayList<>();

                        for (Location location : joinLocations.keySet()) {
                            Player player = joinLocations.get(location);

                            if (player != null)
                                players.add(player);

                            joinLocations.put(location, null);
                        }

                        StaticClasses.dungeonQueuer.startDungeon(players, name);
                    } else {
                        sendAllTitle("&cEntering the dungeon.", "&a" + countdown);
                        sendAllSound(Sound.BLOCK_NOTE_PLING, 1, 1.2F);
                        countdown--;
                    }
                }
            }.runTaskTimer(Carbyne.getInstance(), 0, 20);
        } else
            sendAll("&6&lYou have &7&l" + size + " / " + minJoin + " &6&lrequired to start the dungeon.");
    }

    private void sendAll(String msg) {
        for (Location location : joinLocations.keySet()) {
            Player player = joinLocations.get(location);
            if (player != null)
                MessageManager.sendMessage(player, msg);
        }
    }

    private void sendAllTitle(String title, String subTitle) {
        for (Location location : joinLocations.keySet()) {
            Player player = joinLocations.get(location);
            if (player != null)
                player.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', title)).subtitle(ChatColor.translateAlternateColorCodes('&', subTitle)).stay(7).build());
        }
    }

    private void sendAllSound(Sound sound, float v, float p) {
        for (Location location : joinLocations.keySet()) {
            Player player = joinLocations.get(location);
            if (player != null)
                player.playSound(player.getLocation(), sound, v, p);
        }
    }

    private int size() {
        int size = 0;
        for (Location location : joinLocations.keySet())
            if (joinLocations.get(location) != null)
                size++;
        return size;
    }

    public Trigger getTrigger(String name) {
        for (Trigger trigger : triggers)
            if (trigger.getName().equalsIgnoreCase(name))
                return trigger;
        return null;
    }

    public MythicMob getRandomMob() {
        HashMap<MythicMob, Integer> mobsToSpawn = new HashMap<>();
        int sum = 0;
        for (MobData mobData : mobs.values()) {
            for (MythicMob mob : mobData.getMobs().keySet()) {
                int chance = mobData.getMobs().get(mob);
                sum += chance;
                mobsToSpawn.put(mob, chance);
            }
        }

        int random = Maths.randomNumberBetween(sum, 0);
        int last = 0;

        for (MythicMob mob : mobsToSpawn.keySet()) {
            int value = mobsToSpawn.get(mob);

            if (random >= last && random < value + last)
                return mob;
            else
                last = value + last;
        }

        return null;
    }

    public MythicMob getRandomMob(MobData mobData) {
        HashMap<MythicMob, Integer> mobsToSpawn = new HashMap<>();
        int sum = 0;
        for (MythicMob mob : mobData.getMobs().keySet()) {
            int chance = mobData.getMobs().get(mob);
            sum += chance;
            mobsToSpawn.put(mob, chance);
        }

        int random = Maths.randomNumberBetween(sum, 0);
        int last = 0;

        for (MythicMob mob : mobsToSpawn.keySet()) {
            int value = mobsToSpawn.get(mob);

            if (random >= last && random < value + last)
                return mob;
            else
                last = value + last;
        }

        return null;
    }
}
