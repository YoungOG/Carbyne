package com.medievallords.carbyne.zones;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.region.Selection;
import com.medievallords.carbyne.utils.LocationSerialization;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ZoneManager {

    private final List<Zone> zones = new ArrayList<>();

    public ZoneManager() {
        load();
    }

    public void load() {
        ConfigurationSection cs = Carbyne.getInstance().getZonesFileConfiguration();

        for (String key : cs.getKeys(false)) {
            Location location1 = LocationSerialization.deserializeLocation(cs.getString(key + ".Location1"));
            Location location2 = LocationSerialization.deserializeLocation(cs.getString(key + ".Location2"));
            Selection selection = null;

            if (location1 != null && location2 != null)
                selection = new Selection(location1, location2);

            if (selection != null) {
                Zone zone = new Zone(key, selection);

                int maxMobs = cs.getInt(key + ".MaxMobs");
                double minDistance = cs.getDouble(key + ".MinDistance");
                String displayName = cs.getString(key + ".DisplayName");
                zone.setMaxMobs(maxMobs);
                zone.setMinDistance(minDistance);
                zone.setDisplayName(displayName);

                if (cs.contains(key + ".Mobs")) {
                    HashMap<MythicMob, Integer> mobs = new HashMap<>();

                    for (String s : cs.getStringList(key + ".Mobs")) {
                        String[] split = s.split(",");
                        mobs.put(MythicMobs.inst().getMobManager().getMythicMob(split[0]), Integer.parseInt(split[1]));
                    }

                    zone.getMobs().putAll(mobs);
                }

                zones.add(zone);
            }
        }
    }

    public void reload() {
        try {
            Carbyne.getInstance().setZonesFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getZonesFile()));
            Carbyne.getInstance().getZonesFileConfiguration().save(Carbyne.getInstance().getZonesFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Zone zone : zones)
            zone.setRun(false);

        zones.clear();
        load();
    }

    public void createZone(String name, Selection selection, int maxMobs, double minDistance) {
        Zone zone = new Zone(name, selection);

        zone.setMinDistance(minDistance);
        zone.setMaxMobs(maxMobs);

        zones.add(zone);

        ConfigurationSection cs = Carbyne.getInstance().getZonesFileConfiguration();
        cs = cs.createSection(name);
        cs.set("Location1", LocationSerialization.serializeLocation(selection.location1));
        cs.set("Location2", LocationSerialization.serializeLocation(selection.location2));
        cs.set("MaxMobs", maxMobs);
        cs.set("MinDistance", minDistance);
        cs.set("Mobs", new ArrayList<>());
        cs.set("DisplayName", name);

        try {
            Carbyne.getInstance().getZonesFileConfiguration().save(Carbyne.getInstance().getZonesFile());
            Carbyne.getInstance().setZonesFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getZonesFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Zone getZone(String name) {
        for (Zone zone : zones)
            if (zone.getName().equalsIgnoreCase(name))
                return zone;

        return null;
    }

    public List<Zone> getZones() {
        return zones;
    }
}


