package com.medievallords.carbyne.zones;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class Zone {

    private String name;
    private Location position1, position2;
    private Set<Chunk> chunks = new HashSet<>();
    private int maxMobs, amountOfMobs, minDistance;

    public Zone(String name) {
        this.name = name;
    }

    public void initialize(World world) {
        for (int x = Math.min(position1.getBlockX(), position2.getBlockX()); x < Math.max(position1.getBlockX(), position2.getBlockX()); x += 16) {
            for (int y = Math.min(position1.getBlockZ(), position2.getBlockZ()); y < Math.max(position1.getBlockZ(), position2.getBlockZ()); y += 16) {
                chunks.add(world.getChunkAt(x, y));
            }
        }
    }

   /* public void tick() {
        if (amountOfMobs >= maxMobs) {
            return;
        }

        for (Chunk chunk : chunks) {
            if (chunk.isLoaded()) {
                Location randomLocation = findRandomLocation(chunk);
            }
        }
    }

    private Location findRandomLocation(Chunk chunk) {
        int x = Maths.randomNumberBetween(chunk.getX(), chunk.getX() + 16);
        int z = Maths.randomNumberBetween(chunk.getZ(), chunk.getZ() + 16);
        Player nearestPlayer = findNearestPlayer(location);
        if (nearestPlayer == null) {

        }
    }

    private Player findNearestPlayer(Location location) {
        for (Entity entity : location.getWorld().getNearbyEntities(location, 30, 30, 30)) {
            if (entity instanceof  Player) {
                return (Player) entity;
            }
        }

        return null;
    }*/
}
