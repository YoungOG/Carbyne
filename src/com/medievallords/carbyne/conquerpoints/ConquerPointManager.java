package com.medievallords.carbyne.conquerpoints;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.conquerpoints.objects.ConquerPoint;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class ConquerPointManager {

    private Carbyne main = Carbyne.getInstance();

    @Getter
    private ArrayList<ConquerPoint> conquerPoints = new ArrayList<>();
    @Getter
    private HashMap<UUID, ConquerPoint> inArea = new HashMap<>();

    public ConquerPointManager() {
        loadControlPoints();
    }

    public void loadControlPoints() {
        if (main.getConquerPointsFileConfiguration() == null) {
            reloadConquerPoints();
        }

        ConfigurationSection section = main.getConquerPointsFileConfiguration().getConfigurationSection("conquerpoints");

        if (section == null) {
            return;
        }

        Set<String> conquerPointsSet = section.getKeys(false);
        if (conquerPointsSet != null) {
            for (String conquerPointId : conquerPointsSet) {
                String path = "conquerpoints." + conquerPointId + ".";

                String pos1w = main.getConquerPointsFileConfiguration().getString(path + "pos1.world");
                int pos1x = main.getConquerPointsFileConfiguration().getInt(path + "pos1.x");
                int pos1y = main.getConquerPointsFileConfiguration().getInt(path + "pos1.y");
                int pos1z = main.getConquerPointsFileConfiguration().getInt(path + "pos1.z");

                String pos2w = main.getConquerPointsFileConfiguration().getString(path + "pos2.world");
                int pos2x = main.getConquerPointsFileConfiguration().getInt(path + "pos2.x");
                int pos2y = main.getConquerPointsFileConfiguration().getInt(path + "pos2.y");
                int pos2z = main.getConquerPointsFileConfiguration().getInt(path + "pos2.z");

                Location pos1 = new Location(Bukkit.getWorld(pos1w), pos1x, pos1y, pos1z);
                Location pos2 = new Location(Bukkit.getWorld(pos2w), pos2x, pos2y, pos2z);

                ConquerPoint conquerPoint = new ConquerPoint(conquerPointId, pos1, pos2);

                addConquerPoint(conquerPoint);
            }
        }
    }

    public void saveControlPoints() {
        if (main.getConquerPointsFileConfiguration() == null) {
            reloadConquerPoints();
        }

        if (conquerPoints.size() == 0) {
            return;
        }

        for (ConquerPoint conquerPoint : conquerPoints) {
            String path = "conquerpoints." + conquerPoint.getId() + ".";
            main.getConquerPointsFileConfiguration().set(path + "pos1.world", conquerPoint.getPos1().getWorld().getName());
            main.getConquerPointsFileConfiguration().set(path + "pos1.x", conquerPoint.getPos1().getBlockX());
            main.getConquerPointsFileConfiguration().set(path + "pos1.y", conquerPoint.getPos1().getBlockY());
            main.getConquerPointsFileConfiguration().set(path + "pos1.z", conquerPoint.getPos1().getBlockZ());

            main.getConquerPointsFileConfiguration().set(path + "pos2.world", conquerPoint.getPos2().getWorld().getName());
            main.getConquerPointsFileConfiguration().set(path + "pos2.x", conquerPoint.getPos2().getBlockX());
            main.getConquerPointsFileConfiguration().set(path + "pos2.y", conquerPoint.getPos2().getBlockY());
            main.getConquerPointsFileConfiguration().set(path + "pos2.z", conquerPoint.getPos2().getBlockZ());
        }
        try {
            main.getConquerPointsFileConfiguration().save(main.getConquerPointsFile());
        } catch (Exception e) {
            main.getLogger().log(Level.WARNING, "Failed to save conquerpoints.yml (" + e.getMessage() + ")");
        }
    }

    public void reloadConquerPoints() {
        if (main.getConquerPointsFileConfiguration() == null) {
            main.setConquerPointsFile(new File(main.getDataFolder(), "conquerpoints.yml"));
        }

        main.setConquerPointsFileConfiguration(YamlConfiguration.loadConfiguration(main.getConquerPointsFile()));

        InputStream defConfigStream = main.getResource("conquerpoints.yml");

        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            main.getConquerPointsFileConfiguration().setDefaults(defConfig);
        }
    }

    public void addConquerPoint(ConquerPoint conquerPoint) {
        getConquerPoints().add(conquerPoint);
    }

    public void removeConquerPoint(ConquerPoint conquerPoint) {
        for (ConquerPoint conquerPoint1 : getConquerPoints()) {
            if (conquerPoint == conquerPoint1) {
                getConquerPoints().remove(conquerPoint);
                return;
            }
        }
    }

    public ConquerPoint getConquerPointByName(String id) {
        for (ConquerPoint conquerPoint : getConquerPoints()) {
            if (conquerPoint.getId().equalsIgnoreCase(id)) {
                return conquerPoint;
            }
        }
        return null;
    }

    public boolean isCapturing(Player player) {
        for (ConquerPoint conquerPoint : getConquerPoints()) {
            if (conquerPoint.getHolder().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public ConquerPoint getPlayerConquerPoint(Player player) {
        for (ConquerPoint conquerPoint : getConquerPoints()) {
            if (conquerPoint.getHolder().equals(player.getUniqueId())) {
                return conquerPoint;
            }
        }
        return null;
    }

    public ConquerPoint getConquerPointFromLocation(Location location) {
        for (ConquerPoint conquerPoint : getConquerPoints()) {
            if (isInside(location, conquerPoint.getPos1(), conquerPoint.getPos2())) {
                return conquerPoint;
            }
        }
        return null;
    }

    public boolean isInside(Location location, Location corner1, Location corner2) {
        int x1 = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int y1 = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int z1 = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int x2 = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int y2 = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int z2 = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        return location.getX() >= x1 && location.getX() <= x2 && location.getY() >= y1 && location.getY() <= y2 && location.getZ() >= z1 && location.getZ() <= z2;
    }
}
