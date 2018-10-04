package com.medievallords.carbyne.portals;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Getter
@Setter
public class Portal {

    private String name;

    private Location position1 = new Location(Bukkit.getWorld("world"), 0, 0, 0), position2 = new Location(Bukkit.getWorld("world"), 0, 0, 0);
    private Location targetLocation = new Location(Bukkit.getWorld("world"), -730, 104, 317);

    public Portal(String name) {
        this.name = name;
    }

    public boolean isInPortal(Location location) {
        if (!location.getWorld().equals(position1.getWorld())) {
            return false;
        }

        int minX = Math.min(position1.getBlockX(), position2.getBlockX());
        int minY = Math.min(position1.getBlockY(), position2.getBlockY());
        int minZ = Math.min(position1.getBlockZ(), position2.getBlockZ());
        int maxX = Math.max(position1.getBlockX(), position2.getBlockX());
        int maxY = Math.max(position1.getBlockY(), position2.getBlockY());
        int maxZ = Math.max(position1.getBlockZ(), position2.getBlockZ());
        return (minX <= location.getBlockX() && location.getBlockX() <= maxX && minY <= location.getBlockY() && location.getBlockY() <= maxY && minZ <= location.getBlockZ() && location.getBlockZ() <= maxZ);
    }
}
