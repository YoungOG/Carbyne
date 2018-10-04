package com.medievallords.carbyne.region;


import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class Region {
    private Selection selection;
    private String name;

    public Region(final String name) {
        this.selection = new Selection(null, null);
        this.name = name;
        this.load();
        StaticClasses.gearManager.getNerfedRegions().add(this);
    }

    public String getName() {
        return this.name;
    }

    public static void loadAll() {
        FileConfiguration rfc = Carbyne.getInstance().getGearFileConfiguration();

        if (rfc.contains("NerfedCarbyneRegions"))
            for (final String regionName : rfc.getConfigurationSection("NerfedCarbyneRegions.").getKeys(false))
                new Region(regionName);
    }

    public void load() {
        FileConfiguration rfc = Carbyne.getInstance().getGearFileConfiguration();
        Selection selection = new Selection(null, null);

        if (rfc.contains("NerfedCarbyneRegions." + this.name)) {
            selection.setLocation1(retrieveLocation(rfc, "NerfedCarbyneRegions." + this.name + ".1"));
            selection.setLocation2(retrieveLocation(rfc, "NerfedCarbyneRegions." + this.name + ".2"));
            this.setSelection(selection);
        }
    }

    public Selection getSelection() {
        return this.selection;
    }

    public void setSelection(Selection selection) {
        this.selection = selection;
        this.updateSelection();
    }

    public void updateSelection() {
        FileConfiguration rfc = Carbyne.getInstance().getGearFileConfiguration();

        if (this.getSelection() != null) {
            if (this.getSelection().getLocation1() != null)
                writeLocation(rfc, "NerfedCarbyneRegions." + this.name + ".1", this.getSelection().getLocation1());

            if (this.getSelection().getLocation2() != null)
                writeLocation(rfc, "NerfedCarbyneRegions." + this.name + ".2", this.getSelection().getLocation2());

            try {
                Carbyne.getInstance().getGearFileConfiguration().save(Carbyne.getInstance().getGearFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Region get(String name) {
        for (final Region r : StaticClasses.gearManager.getNerfedRegions())
            if (r.name.equalsIgnoreCase(name))
                return r;

        return null;
    }

    public static Region get(Location location) {
        for (Region r : StaticClasses.gearManager.getNerfedRegions()) {
            if (r.getSelection() != null && r.getSelection().getLocation1() != null && r.getSelection().getLocation2() != null && location.getWorld() == r.getSelection().getLocation1().getWorld()) {
                int minX = min(r.getSelection().getLocation1().getBlockX(), r.getSelection().getLocation2().getBlockX());
                int minY = min(r.getSelection().getLocation1().getBlockY(), r.getSelection().getLocation2().getBlockY());
                int minZ = min(r.getSelection().getLocation1().getBlockZ(), r.getSelection().getLocation2().getBlockZ());
                int maxX = max(r.getSelection().getLocation1().getBlockX(), r.getSelection().getLocation2().getBlockX());
                int maxY = max(r.getSelection().getLocation1().getBlockY(), r.getSelection().getLocation2().getBlockY());
                int maxZ = max(r.getSelection().getLocation1().getBlockZ(), r.getSelection().getLocation2().getBlockZ());

                if (minX <= location.getBlockX() && location.getBlockX() <= maxX && minY <= location.getBlockY() && location.getBlockY() <= maxY && minZ <= location.getBlockZ() && location.getBlockZ() <= maxZ) {
                    return r;
                }
            }
        }

        return null;
    }

    public static int min(final int a, final int b) {
        if (a < b)
            return a;

        return b;
    }

    public static int max(final int a, final int b) {
        if (a > b)
            return a;

        return b;
    }

    public static Location retrieveLocation(final FileConfiguration fileConf, final String path) {
        return fileConf.getVector(String.valueOf(path) + ".coords").toLocation(Bukkit.getWorld(fileConf.getString(String.valueOf(path) + ".world")));
    }

    public static void writeLocation(final FileConfiguration fileConf, final String path, final Location location) {
        fileConf.set(String.valueOf(path) + ".coords", location.toVector());
        fileConf.set(String.valueOf(path) + ".world", location.getWorld().getName());
    }
}
