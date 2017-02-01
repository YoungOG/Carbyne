package com.medievallords.carbyne.gates;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

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

    private String gateId;
    private int delay = 0;
    private HashMap<Location, Boolean> pressurePlateMap = new HashMap<>();
    private ArrayList<Location> buttonLocations = new ArrayList<>();
    private ArrayList<Location> redstoneBlockLocations = new ArrayList<>();

    public Gate(String gateId) {
        this.gateId = gateId;
    }

    public Gate(String id, Gate gate) {
        this.gateId = id;
        this.delay = gate.getDelay();
        this.pressurePlateMap = gate.pressurePlateMap;
        this.redstoneBlockLocations = gate.redstoneBlockLocations;
        this.buttonLocations = gate.buttonLocations;
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
        } else if (activePressurePlates >= pressurePlateMap.keySet().size()) {
            MessageManager.sendMessage(location, 10, "&aThe gate &b" + gateId + " &ahas been opened.");

            openGate();
        } else {
            MessageManager.sendMessage(location, 10, "&aThe gate &b" + gateId + " &ahas been closed.");

            closeGate();
        }
    }

    public void openGate() {
        Block block;

        for (Location location : redstoneBlockLocations) {
            block = location.getBlock();

            if (block.getType() != Material.REDSTONE_BLOCK) {
                block.setType(Material.REDSTONE_BLOCK);
            }
        }
    }

    public void closeGate() {
        Block block;

        for (Location location : redstoneBlockLocations) {
            block = location.getBlock();

            if (block.getType() == Material.REDSTONE_BLOCK) {
                block.setType(Material.AIR);
            }
        }
    }

    public void saveGate() {
        ConfigurationSection section = main.getGateFileConfiguration().getConfigurationSection("Gates");

        if (!section.isSet(gateId)) {
            section.createSection(gateId);
        }

        if (!section.isSet(gateId + ".Delay")) {
            section.createSection(gateId + ".Delay");
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

        section.set(gateId + ".Delay", delay);

        if (pressurePlateMap.keySet().size() > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : pressurePlateMap.keySet()) {
                locationStrings.add(LocationSerialization.serializeLocation(location));
            }

            section.set(gateId + ".PressurePlateLocations", locationStrings);
            System.out.println("Location Strings: " + locationStrings.toString());
        }

        if (buttonLocations.size() > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : buttonLocations) {
                locationStrings.add(LocationSerialization.serializeLocation(location));
            }

            section.set(gateId + ".ButtonLocations", locationStrings);
            System.out.println("Location Strings: " + locationStrings.toString());
        }

        if (redstoneBlockLocations.size() > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : redstoneBlockLocations) {
                locationStrings.add(LocationSerialization.serializeLocation(location));
            }

            section.set(gateId + ".RedstoneBlockLocations", locationStrings);
            System.out.println("Location Strings: " + locationStrings.toString());
        }

        try {
            main.getGateFileConfiguration().save(main.getGateFile());
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.WARNING, "Failed to save gate " + gateId + "!");
        }
    }
}
