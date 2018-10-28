package com.medievallords.carbyne.dungeons.dungeons;


import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.instance.DungeonInstance;
import com.medievallords.carbyne.dungeons.dungeons.options.SpawnMode;
import com.medievallords.carbyne.dungeons.mechanics.Mechanic;
import com.medievallords.carbyne.dungeons.player.DPlayer;
import com.medievallords.carbyne.dungeons.triggers.Trigger;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.WorldLoader;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by WE on 2017-09-27.
 */

@Getter
public class DungeonHandler {

    private final List<Dungeon> dungeons = new ArrayList<>();

    public static final int MAX_INSTANCES = 20;
    public static int INSTANCES = 0;

    private final List<DungeonInstance> instances = new ArrayList<>();

    public DungeonHandler() {
        load();
    }

    public void load() {
        ConfigurationSection cs = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons");

        if (cs == null) {
            Carbyne.getInstance().getDungeonsFileConfiguration().createSection("Dungeons");
            try {
                Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        dungeons.clear();

        for (String key : cs.getKeys(false)) {
            HashMap<Location, MythicMob> spawners = new HashMap<>();
            HashMap<Location, Player> joinLocations = new HashMap<>();
            HashMap<EntityType, MobData> mobs = new HashMap<>();
            HashMap<Location, String> items = new HashMap<>();
            Location spawnLocation = null, readyLocation = null, lobbyLocation = null, exitLocation = null, completeLocation = null;

            World world = null;
            if (cs.getString(key + ".CGWorld") != null)
                world = Bukkit.getWorld(cs.getString(key + ".CGWorld"));

            if (cs.getConfigurationSection(key + ".Mobs") != null) {
                for (String entityName : cs.getConfigurationSection(key + ".Mobs").getKeys(false)) {
                    int maxAmount = 1;
                    HashMap<MythicMob, Integer> mobsToSpawn = new HashMap<>();
                    if (cs.getConfigurationSection(key + ".Mobs." + entityName).contains("List")) {
                        for (String mobString : cs.getConfigurationSection(key + ".Mobs." + entityName).getStringList("List")) {
                            String[] split = mobString.split(",");
                            MythicMob actualMob = MythicMobs.inst().getMobManager().getMythicMob(split[0]);
                            if (actualMob == null)
                                continue;

                            int chance = 1;
                            try {
                                chance = Integer.parseInt(split[1]);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                continue;
                            }

                            mobsToSpawn.put(actualMob, chance);
                        }
                    }

                    if (cs.getConfigurationSection(key + ".Mobs." + entityName).contains("Amount"))
                        maxAmount = cs.getConfigurationSection(key + ".Mobs." + entityName).getInt("Amount");

                    MobData mobData = new MobData(mobsToSpawn, maxAmount);

                    mobs.put(EntityType.valueOf(entityName.toUpperCase()), mobData);
                }
            }

            List<Trigger> triggers = new ArrayList<>();

            if (cs.getConfigurationSection(key + ".Triggers") != null) {
                ConfigurationSection triggerSection = cs.getConfigurationSection(key + ".Triggers");
                for (String triggerName : triggerSection.getKeys(false)) {
                    String locationString = triggerSection.getString(triggerName + ".Location");
                    String type = triggerSection.getString(triggerName + ".Type");

                    List<String> data = triggerSection.contains(triggerName + ".Data") ? triggerSection.getStringList(triggerName + ".Data") : new ArrayList<>();
                    if (locationString != null && type != null) {
                        Location location = LocationSerialization.deserializeLocation(locationString);
                        Trigger trigger = Trigger.getTrigger(triggerName, type, location, new DungeonLineConfig(data));
                        if (trigger == null) {
                            continue;
                        }

                        if (triggerSection.getConfigurationSection(triggerName + ".Mechanics") != null) {
                            ConfigurationSection mechanicSection = triggerSection.getConfigurationSection(triggerName + ".Mechanics");
                            for (String mechanicName : mechanicSection.getKeys(false)) {
                                if (mechanicSection.getStringList(mechanicName) != null) {
                                    List<String> mechanicData = mechanicSection.getStringList(mechanicName);

                                    Mechanic mechanic = Mechanic.getMechanic(mechanicName, mechanicData);
                                    if (mechanic != null) {
                                        Bukkit.getLogger().log(Level.INFO, "&Loaded mechanic: " + mechanicName);
                                        trigger.getMechanics().add(mechanic);
                                    } else {
                                        Bukkit.getLogger().log(Level.WARNING, "&cFailed to load mechanic: " + mechanicName);
                                    }
                                } else {
                                    Bukkit.getLogger().log(Level.WARNING, "&cFailed to load mechanic: " + mechanicName);
                                }
                            }
                        } else {
                            Bukkit.getLogger().log(Level.WARNING, "&cFailed to load mechanics: " + triggerName);
                        }

                        Bukkit.getLogger().log(Level.INFO, "&Loaded trigger: " + triggerName);
                        triggers.add(trigger);
                    } else {
                        Bukkit.getLogger().log(Level.WARNING, "[Trigger] Could not load trigger with name: " + triggerName);
                    }
                }
            }

            boolean pvp = false, spellsAllowed = true;
            SpawnMode spawnMode = SpawnMode.RESPAWN;
            long cooldown = 1800000;

            if (cs.contains(key + ".Spawners"))
                for (String location : cs.getStringList(key + ".Spawners")) {
                    String[] split = location.split(",");
                    MythicMob actualMob = MythicMobs.inst().getMobManager().getMythicMob(split[1]);
                    if (actualMob == null)
                        continue;

                    spawners.put(LocationSerialization.deserializeLocation(split[0]), actualMob);
                }

            if (cs.contains(key + ".PVP"))
                pvp = cs.getBoolean(key + ".PVP");

            if (cs.contains(key + ".SpellsAllowed"))
                spellsAllowed = cs.getBoolean(key + ".SpellsAllowed");

            if (cs.contains(key + ".SpawnMode")) {
                spawnMode = SpawnMode.valueOf(cs.getString(key + ".SpawnMode").toUpperCase());
            }

            if (cs.contains(key + ".Cooldown")) {
                cooldown = cs.getLong(key + ".Cooldown");
            }

            if (cs.contains(key + ".SpawnLocation"))
                spawnLocation = LocationSerialization.deserializeLocation(cs.getString(key + ".SpawnLocation"));

            if (cs.contains(key + ".CompleteLocation"))
                completeLocation = LocationSerialization.deserializeLocation(cs.getString(key + ".CompleteLocation"));

            if (cs.contains(key + ".ReadyLocation"))
                readyLocation = LocationSerialization.deserializeLocation(cs.getString(key + ".ReadyLocation"));

            if (cs.contains(key + ".LobbyLocation"))
                lobbyLocation = LocationSerialization.deserializeLocation(cs.getString(key + ".LobbyLocation"));

            if (cs.contains(key + ".ExitLocation"))
                exitLocation = LocationSerialization.deserializeLocation(cs.getString(key + ".ExitLocation"));

            if (cs.contains(key + ".Items")) {
                for (String s : cs.getStringList(key + ".Items")) {
                    String[] split = s.split(",");
                    items.put(LocationSerialization.deserializeLocation(split[0]), split[1]);
                }
            }

            for (String location : cs.getStringList(key + ".JoiningLocations"))
                joinLocations.put(LocationSerialization.deserializeLocation(location), null);

            List<String> options = cs.getStringList(key + ".Options");
            int minJoin = 10;
            if (cs.contains(key + ".MinJoin")) {
                minJoin = cs.getInt(key + ".MinJoin");
            }

            Dungeon dungeon = new Dungeon(key, options != null ? new DungeonLineConfig(options) : new DungeonLineConfig(new ArrayList<>()));
            dungeon.setWorld(world);
            dungeon.setSpawners(spawners);
            dungeon.setJoinLocations(joinLocations);
            dungeon.setMinJoin(minJoin);
            dungeon.setMobs(mobs);
            dungeon.setSpawnLocation(spawnLocation);
            dungeon.setReadyLocation(readyLocation);
            dungeon.setLobbyLocation(lobbyLocation);
            dungeon.setExitLocation(exitLocation);
            dungeon.setCompleteLocation(completeLocation);
            dungeon.setLootChests(items);
            dungeon.setCooldown(cooldown);
            dungeon.setPvp(pvp);
            dungeon.setSpellsAllowed(spellsAllowed);
            dungeon.setSpawnMode(spawnMode);
            dungeon.setTriggers(triggers);

            dungeons.add(dungeon);
        }
    }

    public void reload() {
        cancelAll();
        saveConfig();
        load();
    }

    private void saveConfig() {
        // main.setDungeonsFile(new File(main.getDataFolder(), "dungeons.yml"));
        // main.setDungeonsFileConfiguration(YamlConfiguration.loadConfiguration(main.getDungeonsFile()));

        try {
            Carbyne.getInstance().setDungeonsFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getDungeonsFile()));
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDungeon(String name) {
        Dungeon dungeon = new Dungeon(name, new DungeonLineConfig(new ArrayList<>()));
        dungeons.add(dungeon);
        ConfigurationSection cs = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons").createSection(name);

        cs.set("Spawners", new ArrayList<>());
        cs.set("SpawnLocation", "");
        cs.set("LobbyLocation", "");
        cs.set("ReadyLocation", "");
        cs.set("ExitLocation", "");
        cs.set("CompleteLocation", "");
        cs.set("JoiningLocations", new ArrayList<>());
        cs.set("MinJoin", 10);
        cs.set("Mobs", "");
        cs.set("CGWorld", "");
        cs.set("Items", new ArrayList<>());
        cs.set("Cooldown", 1800000);
        cs.set("PVP", false);
        cs.set("SpawnMode", "RESPAWN");

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Dungeon getDungeon(String name) {
        for (Dungeon dungeon : dungeons) {
            if (dungeon.getName().equalsIgnoreCase(name)) {
                return dungeon;
            }
        }

        return null;
    }

    public Dungeon getDungeon(Location location) {
        for (Dungeon dungeon : dungeons) {
            if (dungeon.getJoinLocations().containsKey(location)) {
                return dungeon;
            }
        }

        return null;
    }

    public DPlayer getDPlayer(UUID uuid) {
        for (DungeonInstance instance : instances) {
            for (int l = 0; l < instance.getPlayers().size(); l++) {
                DPlayer dPlayer = instance.getPlayers().get(l);

                if (dPlayer.getBukkitPlayer().getUniqueId().equals(uuid)) {
                    return dPlayer;
                }
            }
        }

        return null;
    }

    public DungeonInstance getInstance(int id) {
        for (DungeonInstance instance : instances) {
            if (instance.getId() == id) {
                return instance;
            }
        }

        return null;
    }

    public DungeonInstance getInstance(World world) {
        for (DungeonInstance instance : instances) {
            if (instance.getWorld().getName().equalsIgnoreCase(world.getName())) {
                return instance;
            }
        }

        return null;
    }

    public void cancelAll() {
        for (DungeonInstance instance : instances) {
            World world = instance.getWorld();

            for (int i = world.getPlayers().size() - 1; i >= 0; i--) {
                Player player = world.getPlayers().get(i);
                player.teleport(new Location(Bukkit.getWorld("world"), -729.5, 104, 317.5));
            }

            Bukkit.unloadWorld(world, false);
            WorldLoader.deleteWorld(world.getWorldFolder());
            Bukkit.getWorlds().remove(world);
        }

        instances.clear();
    }

    public static final String[] EXCLUDED_FILES = {"uid.dat", "data"};
}
