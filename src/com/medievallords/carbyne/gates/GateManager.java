package com.medievallords.carbyne.gates;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.LocationSerialization;
import org.bukkit.Location;
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
                int delay = section.getInt(id + ".Delay");
                HashMap<Location, Boolean>  pressurePlateLocations = new HashMap<>();
                ArrayList<Location> buttonLocations = new ArrayList<>();
                ArrayList<Location> redstoneBlockLocations = new ArrayList<>();

                for (String s : section.getStringList(id + ".PressurePlateLocations")) {
                    pressurePlateLocations.put(LocationSerialization.deserializeLocation(s), false);
                }

                for (String s : section.getStringList(id + ".ButtonLocations")) {
                    buttonLocations.add(LocationSerialization.deserializeLocation(s));
                }

                for (String s : section.getStringList(id + ".RedstoneBlockLocations")) {
                    redstoneBlockLocations.add(LocationSerialization.deserializeLocation(s));
                }

                Gate gate = new Gate(id);
                gate.setDelay(delay);
                gate.setButtonLocations(buttonLocations);
                gate.setPressurePlateMap(pressurePlateLocations);
                gate.setRedstoneBlockLocations(redstoneBlockLocations);

                gates.add(gate);
            }

            main.getLogger().log(Level.INFO, "Successfully loaded " + gates.size() + " gates.");
        }
    }

    public void saveGates(){
        for(Gate gate:gates){
            gate.saveGate();
        }
    }

    public ArrayList<Gate> getGates() {
        return gates;
    }
}
