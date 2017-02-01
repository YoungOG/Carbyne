package com.medievallords.carbyne.gates;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.LocationSerialization;
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

    //Gate ID, used to identify gates.
    private String gateId;
    //Delay happens when all pressure plates are pressed down, it will do the delay before opening the gate.
    private int delay;
    //PressurePlateMap is used to map the pressure plates of this gate, and the boolean represent whether the pressure plate is activated currently or not.
    private HashMap<Location, Boolean> pressurePlateMap;
    //Button Locations is a list of buttons used to open the gate instantly.
    private ArrayList<Location> buttonLocations;
    //Redstone Block Locations is a list of redstone blocks to be activated.
    private ArrayList<Location> redstoneBlockLocations;

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

    public void pressurePlateActivated(Block block) {

    }

    public void openGate() {
        Block block;

        for (Location location:redstoneBlockLocations) {
            block = location.getBlock();

            if (block.getType() != Material.REDSTONE_BLOCK) {
                block.setType(Material.REDSTONE_BLOCK);
            }
        }
    }

    public void closeGate() {
        Block block;

        for (Location location:redstoneBlockLocations) {
            block = location.getBlock();

            if (block.getType() == Material.REDSTONE_BLOCK) {
                block.setType(Material.AIR);
            }
        }
    }

    public void saveGate() {
        ConfigurationSection section = main.getGateData().getConfigurationSection("Gates");

        if (!section.isSet(gateId)) {
            section.createSection(gateId);
        }

        if (!section.isSet(gateId + ".Delay")) {
            section.createSection(gateId + ".Delay");
        }

        if (!section.isSet(gateId + ".PressurePlateLocations")) {
            section.createSection(gateId + ".PressurePlateLocations");
        }

        if (!section.isSet(gateId + ".ButtonLocations")) {
            section.createSection(gateId + ".ButtonLocations");
        }

        if (!section.isSet(gateId + ".RedstoneBlockLocations")) {
            section.createSection(gateId + ".RedstoneBlockLocations");
        }
        section.set(gateId + ".Delay", delay);

        ArrayList<String> locationStrings = new ArrayList<>();

        if (pressurePlateMap.keySet().size() > 0) {
            for (Location location : pressurePlateMap.keySet()) {
                locationStrings.add(LocationSerialization.serializeLocation(location));
            }
        }

        section.set(gateId + ".PressurePlateLocations", locationStrings);

        locationStrings.clear();

        if (buttonLocations.size() > 0) {
            for (Location location : buttonLocations) {
                locationStrings.add(LocationSerialization.serializeLocation(location));
            }
        }

        section.set(gateId + ".ButtonLocations", locationStrings);

        locationStrings.clear();

        if (redstoneBlockLocations.size() > 0) {
            for (Location location : redstoneBlockLocations) {
                locationStrings.add(LocationSerialization.serializeLocation(location));
            }
        }

        section.set(gateId + ".RedstoneBlockLocations", locationStrings);

        try {
            main.getGateData().save(main.getGateFile());
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.WARNING, "Failed to save gate " + gateId + "!");
        }
    }
}
