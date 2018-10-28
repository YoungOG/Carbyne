package com.medievallords.carbyne.portals;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.LocationSerialization;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.HashMap;

public class PortalManager {

    @Getter
    private final HashMap<String, Portal> portals = new HashMap<>();

    public PortalManager() {
        load();
    }

    public void load() {
        ConfigurationSection cs = Carbyne.getInstance().getPortalsFileConfiguration().getConfigurationSection("Portals");

        if (cs == null) {
            Carbyne.getInstance().getPortalsFileConfiguration().createSection("Portals");
            try {
                Carbyne.getInstance().getPortalsFileConfiguration().save(Carbyne.getInstance().getPortalsFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        portals.clear();

        for (String key : cs.getKeys(false)) {
            Location targetLocation = null, position1 = null, position2 = null;

            if (cs.contains(key + ".Position1")) {
                position1 = LocationSerialization.deserializeLocation(cs.getString(key + ".Position1"));
            }

            if (cs.contains(key + ".Position2")) {
                position2 = LocationSerialization.deserializeLocation(cs.getString(key + ".Position2"));
            }

            if (cs.contains(key + ".TargetLocation")) {
                targetLocation = LocationSerialization.deserializeLocation(cs.getString(key + ".TargetLocation"));
            }

            if (targetLocation == null) {
                System.out.println("Could not load portal: " + key + ". A target location was not found.");
                continue;
            }

            if (position1 == null || position2 == null) {
                System.out.println("Could not load portal: " + key + ". The portal selection was not found.");
                continue;
            }

            Portal portal = new Portal(key);
            portal.setPosition1(position1);
            portal.setPosition2(position2);
            portal.setTargetLocation(targetLocation);

            portals.put(key.toLowerCase(), portal);
        }
    }

    public Portal getPortal(String name) {
        return portals.get(name.toLowerCase());
    }

    public Portal getPortal(Location location) {
        for (Portal portal : portals.values()) {
            if (portal.isInPortal(location)) {
                return portal;
            }
        }

        return null;
    }
}

