package com.medievallords.carbyne.gates;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.LocationSerialization;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Created by Calvin on 1/18/2017
 * for the Carbyne-Gear project.
 */
public class GateManager {

    private Carbyne main = Carbyne.getInstance();
    private ArrayList<Gate> gates = new ArrayList<>();

    public GateManager() {
        loadGates();
    }

    public Gate getGate(String gateId) {
        for (Gate gate : gates) {
            if (gate.getGateId().equalsIgnoreCase(gateId)) {
                return gate;
            }
        }

        return null;
    }

    public Gate getGate(Location location) {
        for (Gate gate : gates) {
            if (gate.getButtonLocations().contains(location) || gate.getPressurePlateMap().containsKey(location) || gate.getRedstoneBlockLocations().contains(location)) {
                return gate;
            }
        }

        return null;
    }

    public void loadGates() {
        ConfigurationSection section = main.getGateFileConfiguration().getConfigurationSection("Gates");

        if (section.getKeys(false).size() > 0) {
            main.getLogger().log(Level.INFO, "Preparing to load " + section.getKeys(false).size() + " gates.");

            for (String id : section.getKeys(false)) {
                int activeLength = section.getInt(id + ".ActiveLength");
                HashMap<Location, Boolean>  pressurePlateLocations = new HashMap<>();
                ArrayList<Location> buttonLocations = new ArrayList<>();
                ArrayList<Location> redstoneBlockLocations = new ArrayList<>();
                HashMap<String, MythicSpawner> mythicSpawners = new HashMap<>();

                for (String s : section.getStringList(id + ".MythicSpawnerNames")) {
                    mythicSpawners.put(s, null);
                }

                for (String s : section.getStringList(id + ".PressurePlateLocations")) {
                    pressurePlateLocations.put(LocationSerialization.deserializeLocation(s), false);
                }

                for (String s : section.getStringList(id + ".ButtonLocations")) {
                    buttonLocations.add(LocationSerialization.deserializeLocation(s));
                }

                for (String s : section.getStringList(id + ".RedstoneBlockLocations")) {
                    redstoneBlockLocations.add(LocationSerialization.deserializeLocation(s));

                    if (LocationSerialization.deserializeLocation(s).getBlock().getType() == Material.REDSTONE_BLOCK) {
                        LocationSerialization.deserializeLocation(s).getBlock().setType(Material.AIR);
                    }
                }

                Gate gate = new Gate(id);
                gate.setActiveLength(activeLength);
                gate.setCurrentLength(activeLength);
                gate.setButtonLocations(buttonLocations);
                gate.setPressurePlateMap(pressurePlateLocations);
                gate.setRedstoneBlockLocations(redstoneBlockLocations);

                for (MythicSpawner mythicSpawner : MythicMobs.inst().getSpawnerManager().listSpawners) {
                    for (String spawnerName : mythicSpawners.keySet()) {
                        if (mythicSpawner.getInternalName().equalsIgnoreCase(spawnerName)) {
                            mythicSpawners.put(spawnerName, mythicSpawner);
                        }
                    }
                }

                gate.setMythicSpawners(mythicSpawners);

                gate.closeGate();

                gates.add(gate);
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + gates.size() + " gates.");
        }
    }

    public void saveGates() {
        for (Gate gate : gates) {
            gate.saveGate();
        }
    }

    public ArrayList<Gate> getGates() {
        return gates;
    }
}
