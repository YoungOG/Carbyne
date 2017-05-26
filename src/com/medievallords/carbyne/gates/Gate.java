package com.medievallords.carbyne.gates;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.heartbeat.Heartbeat;
import com.medievallords.carbyne.heartbeat.HeartbeatTask;
import com.medievallords.carbyne.heartbeat.blockqueue.BlockType;
import com.medievallords.carbyne.heartbeat.blockqueue.HeartbeatBlockQueue;
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
public class Gate implements HeartbeatTask {

    private Carbyne main = Carbyne.getInstance();

    private int activeLength;
    private int currentLength;
    private String gateId;
    private HashMap<Location, Boolean> pressurePlateMap = new HashMap<>();
    private ArrayList<Location> buttonLocations = new ArrayList<>();
    private ArrayList<Location> redstoneBlockLocations = new ArrayList<>();
    private HashMap<String, MythicSpawner> mythicSpawners = new HashMap<>();
    private Heartbeat heartbeat;
    private boolean open = false;
    private boolean keepOpen = false;
    private boolean keepClosed = false;

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
        pressurePlateMap.put(location, active);

        int activePressurePlates = 0;

        for (Location locations : pressurePlateMap.keySet()) {
            if (pressurePlateMap.get(locations)) {
                activePressurePlates++;
            }
        }

        if (activePressurePlates < pressurePlateMap.keySet().size()) {
            MessageManager.sendMessage(location, 10, "&aThere are &e" + activePressurePlates + "/" + pressurePlateMap.keySet().size() + " &aPressure Plates needed to open &b" + gateId + "&a.");

            JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&aThere are &e" + activePressurePlates + "/" + pressurePlateMap.keySet().size() + " &aplayers needed to open &b" + gateId + "&a."))
                    .actionbar(PlayerUtility.getPlayersInRadius(location, 10).toArray(new Player[PlayerUtility.getPlayersInRadius(location, 10).size()]));
        }

        if (activePressurePlates >= pressurePlateMap.keySet().size()) {
            MessageManager.sendMessage(location, 10, "&aThe gate &b" + gateId + " &ahas been opened.");

            JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&aThe gate &b" + gateId + " &ahas been opened."))
                    .actionbar(PlayerUtility.getPlayersInRadius(location, 10).toArray(new Player[PlayerUtility.getPlayersInRadius(location, 10).size()]));

            openGate();
        }
    }

    public void buttonActivated(Location location) {
        MessageManager.sendMessage(location, 10, "&aThe gate &b" + gateId + " &ahas been opened.");

        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&aThe gate &b" + gateId + " &ahas been opened."))
                .actionbar(PlayerUtility.getPlayersInRadius(location, 10).toArray(new Player[PlayerUtility.getPlayersInRadius(location, 10).size()]));

        openGate();
    }

    public synchronized void openGate() {
        if (keepClosed) {
            return;
        }

        open = true;

        this.currentLength = this.activeLength;

        if (redstoneBlockLocations.size() > 0) {
            for (Location location : redstoneBlockLocations) {
                if (location != null) {
                    Block block = location.getBlock();

                    if (block != null) {
                        if (block.getType() != Material.REDSTONE_BLOCK) {
                            HeartbeatBlockQueue.types.add(new BlockType(Material.REDSTONE_BLOCK, block.getLocation()));
                        }
                    }
                }
            }
        }

        if (this.heartbeat == null) {
            this.heartbeat = new Heartbeat(this, 1000L);
            heartbeat.start();
        }
    }

    public synchronized void closeGate() {
        open = false;
        keepOpen = false;
        currentLength = 0;

        try {
            for (Location location : redstoneBlockLocations) {
                Block block = location.getBlock();

                if (block.getType() == Material.REDSTONE_BLOCK) {
                    HeartbeatBlockQueue.types.add(new BlockType(Material.AIR, block.getLocation()));
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void saveGate() {
        ConfigurationSection section = main.getGateFileConfiguration().getConfigurationSection("Gates");

        if (!section.isSet(gateId)) {
            section.createSection(gateId);
        }

        if (!section.isSet(gateId + ".ActiveLength")) {
            section.createSection(gateId + ".ActiveLength");
        }

        if (!section.isSet(gateId + ".MythicSpawnerNames")) {
            section.createSection(gateId + ".MythicSpawnerNames");
        }

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

        if (mythicSpawners.keySet().size() > 0) {
            ArrayList<String> spawnerNames = new ArrayList<>();

            for (String s : mythicSpawners.keySet()) {
                spawnerNames.add(s);
            }

            section.set(gateId + ".MythicSpawnerNames", spawnerNames);
        }

        if (pressurePlateMap.keySet().size() > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : pressurePlateMap.keySet()) {
                locationStrings.add(LocationSerialization.serializeLocation(location));
            }

            section.set(gateId + ".PressurePlateLocations", locationStrings);
        }

        if (buttonLocations.size() > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : buttonLocations) {
                locationStrings.add(LocationSerialization.serializeLocation(location));
            }

            section.set(gateId + ".ButtonLocations", locationStrings);
        }

        if (redstoneBlockLocations.size() > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : redstoneBlockLocations) {
                locationStrings.add(LocationSerialization.serializeLocation(location));
            }

            section.set(gateId + ".RedstoneBlockLocations", locationStrings);
        }

        try {
            main.getGateFileConfiguration().save(main.getGateFile());
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.WARNING, "Failed to save gate " + gateId + "!");
        }
    }

    @Override
    public boolean heartbeat() {
        int pressedPressurePlates = 0;

        if (keepOpen) {
            currentLength = activeLength;
        }

        for (Location location : pressurePlateMap.keySet()) {
            if (pressurePlateMap.get(location)) {
                pressedPressurePlates++;
            }
        }

        if (pressedPressurePlates >= pressurePlateMap.keySet().size()) {
            openGate();
        }

        if (currentLength-- > 0) {
            return true;
        } else {
            closeGate();
            heartbeat = null;
            return false;
        }
    }

    public void killMob() {
        int totalMobs = 0;

        for (MythicSpawner spawner : mythicSpawners.values()) {
            totalMobs += spawner.getNumberOfMobs();
        }

        if (!open) {
            if (totalMobs - 1 <= 0) {
                keepOpen = true;
                openGate();

                MessageManager.sendMessage(getLocaton(), 20, "&aThe gate &b" + gateId + " &ahas been opened.");

                JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&aThe gate &b" + gateId + " &ahas been opened."))
                        .actionbar(PlayerUtility.getPlayersInRadius(getLocaton(), 20).toArray(new Player[PlayerUtility.getPlayersInRadius(getLocaton(), 20).size()]));

                openGate();
            }
        }
    }

    public void addMob() {
        closeGate();
    }

    public Location getLocaton() {
        return redstoneBlockLocations.get(0).getBlock().getLocation();
    }
}
