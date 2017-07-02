package com.medievallords.carbyne.controlpoints;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by Williams on 2017-06-11
 * for the Carbyne project.
 */
public class ControlPointManager {

    private Carbyne main = Carbyne.getInstance();
    public ArrayList<ControlPoint> controlPoints = new ArrayList<>();

    public ControlPointManager() {
        load();
    }

    public void load() {

        if (!controlPoints.isEmpty()) {
            for (ControlPoint controlPoint : controlPoints) {
                controlPoint.stopCountdown();
                controlPoint.stopTimer();
            }
        }
        controlPoints.clear();

        ConfigurationSection cp = main.getControlPointsFileConfiguration().getConfigurationSection("ControlPoints");

        if (cp == null) {
            main.getControlPointsFileConfiguration().createSection("ControlPoints");

            saveAndLoadConfig();

            return;
        }

        for (String controlPoint : cp.getKeys(false)) {
             Location location = (Location) cp.get(controlPoint + ".Location");
             int maxTime = cp.getInt(controlPoint + ".MaxTime");
             List<String> rewards = cp.getStringList(controlPoint + ".Rewards");
             String displayName = cp.getString(controlPoint + ".DisplayName");

             if (location == null || maxTime <= 0) {
                 continue;
             }

             ControlPoint newControlPoint = new ControlPoint(controlPoint, location, maxTime, displayName != null ? displayName : "");
             controlPoints.add(newControlPoint);

            if (rewards != null && !rewards.isEmpty()) {
                newControlPoint.rewards = rewards;
            }
        }
    }

    public void createControlPoint(Player player, String name, Location location, int maxTime) {
        if (getControlPoint(name) != null) {
            MessageManager.sendMessage(player, "A controlpoint with this name already exists");
            return;
        }

        for (ControlPoint controlPoint : controlPoints) {
            if (controlPoint.getLocation().equals(location)) {
                MessageManager.sendMessage(player, "A controlpoint with this location already exists");
                return;
            }
        }

        ConfigurationSection cp = main.getControlPointsFileConfiguration().getConfigurationSection("ControlPoints");

        if (cp == null) {
            main.getControlPointsFileConfiguration().createSection("ControlPoints");

            saveAndLoadConfig();
        }

        controlPoints.add(new ControlPoint(name, location, maxTime));

        main.getControlPointsFileConfiguration().getConfigurationSection("ControlPoints").createSection(name);
        main.getControlPointsFileConfiguration().set("ControlPoints." + name + ".Location", location);
        main.getControlPointsFileConfiguration().set("ControlPoints." + name + ".MaxTime", maxTime);
        main.getControlPointsFileConfiguration().set("ControlPoints." + name + ".DisplayName", "");
        main.getControlPointsFileConfiguration().set("ControlPoints." + name + ".Rewards", "");

        saveAndLoadConfig();
    }

    public void createControlPoint(Player player, String name, Location location, int maxTime, String displayName) {
        if (getControlPoint(name) != null) {
            MessageManager.sendMessage(player, "A controlpoint with this name already exists");
            return;
        }

        for (ControlPoint controlPoint : controlPoints) {
            if (controlPoint.getLocation().equals(location)) {
                MessageManager.sendMessage(player, "A controlpoint with this location already exists");
                return;
            }
        }

        ConfigurationSection cp = main.getControlPointsFileConfiguration().getConfigurationSection("ControlPoints");

        if (cp == null) {
            main.getControlPointsFileConfiguration().createSection("ControlPoints");

            saveAndLoadConfig();
        }

        controlPoints.add(new ControlPoint(name, location, maxTime, displayName));

        main.getControlPointsFileConfiguration().getConfigurationSection("ControlPoints").createSection(name);
        main.getControlPointsFileConfiguration().set("ControlPoints." + name + ".Location", location);
        main.getControlPointsFileConfiguration().set("ControlPoints." + name + ".MaxTime", maxTime);
        main.getControlPointsFileConfiguration().set("ControlPoints." + name + ".DisplayName", displayName);
        main.getControlPointsFileConfiguration().set("ControlPoints." + name + ".Rewards", "");

        saveAndLoadConfig();
    }

    public void removeControlPoint(Player player, String name) {
        ControlPoint controlPoint = getControlPoint(name);

        if (controlPoint == null) {
            MessageManager.sendMessage(player, "&cCould not find a control point with the name: &b" + name);
            return;
        }

        main.getControlPointsFileConfiguration().set("ControlPoints." + name, null);
        controlPoints.remove(controlPoint);
        saveAndLoadConfig();
        MessageManager.sendMessage(player, "&aControlpoint has been removed");

    }

    public ControlPoint getControlPoint(String name) {
        for (ControlPoint controlPoint : controlPoints) {
            if (controlPoint.getName().equalsIgnoreCase(name)) {
                return controlPoint;
            }
        }
        return null;
    }

    public ControlPoint getControlPoint(UUID uuid) {
        for  (ControlPoint controlPoint : controlPoints) {
            if (controlPoint.getCapper().equals(uuid)) {
                return controlPoint;
            }
        }
        return null;
    }

    public void saveAndLoadConfig() {
        try {
            main.getControlPointsFileConfiguration().save(main.getControlPointsFile());
            main.setControlPointsFileConfiguration(YamlConfiguration.loadConfiguration(main.getControlPointsFile()));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not save and load controlpoints.yml");
        }
    }
}
