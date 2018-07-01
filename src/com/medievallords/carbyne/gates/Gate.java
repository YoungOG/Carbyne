package com.medievallords.carbyne.gates;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Created by Calvin and Chris on 1/18/2017
 * for the Carbyne-Gear project.
 */

@Getter
@Setter
public class Gate {

    private Carbyne main = Carbyne.getInstance();

    private int activeLength;
    private int currentLength;
    private String gateId;
    private HashMap<Location, Boolean> pressurePlateMap = new HashMap<>();
    private ArrayList<Location> buttonLocations = new ArrayList<>();
    private ArrayList<Location> redstoneBlockLocations = new ArrayList<>();
    private HashMap<String, MythicSpawner> mythicSpawners = new HashMap<>();
    private boolean open = false;
    private boolean keepOpen = false, buttonActivatedB = false;
    private BukkitTask bukkitTask;

    public Gate(String gateId) {
        this.gateId = gateId;
        this.currentLength = this.activeLength;
        closeGate();
    }

    public Gate(String id, Gate gate) {
        this.gateId = id;
        this.pressurePlateMap = gate.pressurePlateMap;
        this.redstoneBlockLocations = gate.redstoneBlockLocations;
        this.buttonLocations = gate.buttonLocations;
        this.activeLength = gate.activeLength;
        this.currentLength = gate.activeLength;
        closeGate();
    }

    public void pressurePlateActivated(Location location, boolean active) {
        int totalPressurePlates = pressurePlateMap.size();
        int activatedPreviously = getActivatedPressurePlates();

        pressurePlateMap.put(location, active);

        int activatedCurrently = getActivatedPressurePlates();

        if (activatedCurrently == totalPressurePlates) {
            if (!open) {
                MessageManager.sendMessage(getLocaton(), 20, "&aThe gate has been opened.");
                JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&aThe gate has been opened."))
                        .actionbar(PlayerUtility.getPlayersInRadius(location, 20).toArray(new Player[0]));

                openGate();
            }
        } else if (activatedPreviously == totalPressurePlates) {
            if (open && getMobs() != 0) {
                this.currentLength = this.activeLength;
                bukkitTask = new BukkitRunnable() {
                    private int cooldown = activeLength;

                    @Override
                    public void run() {
                        int activatedCurrently = getActivatedPressurePlates();
                        if (activatedCurrently == totalPressurePlates) {
                            this.cancel();
                            return;
                        }

                        if (buttonActivatedB) {
                            this.cancel();
                            return;
                        }

                        if (cooldown-- == 0) {
                            closeGate();
                            this.cancel();
                        }

                    }
                }.runTaskTimer(main, 0, 20L);

                MessageManager.sendMessage(getLocaton(), 20, "&aThere are &e" + activatedCurrently + "/" + totalPressurePlates + " &aplayers needed to open the gate");
                JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&aThere are &e" + activatedCurrently + "/" + totalPressurePlates + " &aplayers needed to open the gate"))
                        .actionbar(PlayerUtility.getPlayersInRadius(location, 20).toArray(new Player[0]));
            }
        } else {
            MessageManager.sendMessage(getLocaton(), 20, "&aThere are &e" + activatedCurrently + "/" + totalPressurePlates + " &aplayers needed to open the gate");
            JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&aThere are &e" + activatedCurrently + "/" + totalPressurePlates + " &aplayers needed to open the gate"))
                    .actionbar(PlayerUtility.getPlayersInRadius(location, 20).toArray(new Player[0]));
        }
    }

    public void buttonActivated(Location location, boolean active) {
        buttonActivatedB = active;
        if (active) {
            if (!open) {
                MessageManager.sendMessage(location, 20, "&aThe gate has been opened.");
                JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&aThe gate has been opened."))
                        .actionbar(PlayerUtility.getPlayersInRadius(location, 20).toArray(new Player[0]));

                openGate();
            }
        } else {
            this.currentLength = this.activeLength;
            bukkitTask = new BukkitRunnable() {
                private int cooldown = activeLength;

                @Override
                public void run() {
                    int activatedCurrently = getActivatedPressurePlates();
                    if (activatedCurrently == pressurePlateMap.size()) {
                        this.cancel();
                        return;
                    }

                    if (buttonActivatedB) {
                        this.cancel();
                        return;
                    }

                    if (cooldown-- == 0) {
                        closeGate();
                        this.cancel();
                    }

                }
            }.runTaskTimer(main, 0, 20L);
        }
    }

    public synchronized void openGate() {
//        Bukkit.broadcastMessage(gateId + " - Open: " + open);
//        Bukkit.broadcastMessage(gateId + " - KeepingOpen: " + keepOpen);
        open = true;

        if (redstoneBlockLocations.size() > 0)
            for (Location location : redstoneBlockLocations)
                if (location != null && location.getChunk().isLoaded()) {
                    Block block = location.getBlock();

                    if (block != null)
                        if (block.getType() != Material.REDSTONE_BLOCK)
                            location.getBlock().setType(Material.REDSTONE_BLOCK);
                }
    }

    public synchronized void closeGate() {
        open = false;
        keepOpen = false;
        currentLength = 0;

        try {
            for (Location location : redstoneBlockLocations)
                if (location != null && location.getChunk().isLoaded()) {
                    Block block = location.getBlock();

                    if (block.getType() == Material.REDSTONE_BLOCK)
                        location.getBlock().setType(Material.AIR);
                }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void saveGate() {
        ConfigurationSection section = main.getGatesFileConfiguration().getConfigurationSection("Gates");

        if (!section.isSet(gateId))
            section.createSection(gateId);

        if (!section.isSet(gateId + ".ActiveLength"))
            section.createSection(gateId + ".ActiveLength");

        if (!section.isSet(gateId + ".MythicSpawnerNames"))
            section.createSection(gateId + ".MythicSpawnerNames");

        if (!section.isSet(gateId + ".PressurePlateLocations")) {
            section.createSection(gateId + ".PressurePlateLocations");
            section.set(gateId + ".PressurePlateLocations", new ArrayList<String>());
        }

        if (!section.isSet(gateId + ".ButtonLocations")) {
            section.createSection(gateId + ".ButtonLocations");
            section.set(gateId + ".ButtonLocations", new ArrayList<String>());
        }

        if (!section.isSet(gateId + ".RedstoneBlockLocations")) {
            section.createSection(gateId + ".RedstoneBlockLocations");
            section.set(gateId + ".RedstoneBlockLocations", new ArrayList<String>());
        }

        section.set(gateId + ".ActiveLength", activeLength);

        if (mythicSpawners.keySet().size() > 0)
            section.set(gateId + ".MythicSpawnerNames", new ArrayList<>(mythicSpawners.keySet()));

        if (pressurePlateMap.keySet().size() > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : pressurePlateMap.keySet())
                locationStrings.add(LocationSerialization.serializeLocation(location));

            section.set(gateId + ".PressurePlateLocations", locationStrings);
        }

        if (buttonLocations.size() > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : buttonLocations)
                locationStrings.add(LocationSerialization.serializeLocation(location));

            section.set(gateId + ".ButtonLocations", locationStrings);
        }

        if (redstoneBlockLocations.size() > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : redstoneBlockLocations)
                locationStrings.add(LocationSerialization.serializeLocation(location));

            section.set(gateId + ".RedstoneBlockLocations", locationStrings);
        }

        try {
            main.getGatesFileConfiguration().save(main.getGatesFile());
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.WARNING, "Failed to save gate " + gateId + "!");
        }
    }

    public void killMob() {
        int mobs = getMobs();

        if (mobs == -1)
            return;

        if (!open)
            if (mobs == 0) {
                keepOpen = true;
                openGate();

                MessageManager.sendMessage(getLocaton(), 20, "&aThe gate has been opened.");

                JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&aThe gate has been opened."))
                        .actionbar(PlayerUtility.getPlayersInRadius(getLocaton(), 20).toArray(new Player[0]));
            }
    }

    public void addMob() {
        if (pressurePlateMap.isEmpty()) {
            closeGate();
        } else {
            if (getActivatedPressurePlates() < pressurePlateMap.size())
                closeGate();
        }
    }

    private int getActivatedPressurePlates() {
        int activePressurePlates = 0;

        for (Location locations : pressurePlateMap.keySet())
            if (pressurePlateMap.get(locations))
                activePressurePlates++;

        return activePressurePlates;
    }

    private int getMobs() {
        if (mythicSpawners.isEmpty())
            return -1;
        else {
            int totalMobs = 0;

            for (MythicSpawner spawner : mythicSpawners.values())
                totalMobs += spawner.getNumberOfMobs();

            return totalMobs;
        }
    }

    public Location getLocaton() {
        return redstoneBlockLocations.get(0).getBlock().getLocation();
    }
}
