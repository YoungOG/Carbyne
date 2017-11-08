package com.medievallords.carbyne.events.implementations.object;

import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dalton on 8/19/2017.
 */
@Getter
public class LastAliveObject {

    private static int lastAliveNum = 0;

    public static List<LastAliveObject> lastAliveObjects = new ArrayList<>();

    public static LastAliveObject getLastAliveObject(String name) {
        for (LastAliveObject lastAliveObject : lastAliveObjects) {
            String currentCompare = MessageManager.stripStringOfAmpersandColors(lastAliveObject.getName());
            if (name.equalsIgnoreCase(currentCompare)) return lastAliveObject;
        }
        return null;
    }

    private final int lastAliveType;

    private boolean ready;
    private String name;
    private Location lobby;
    private List<Location> spawnLocations;

    public LastAliveObject(boolean ready, String name, Location lobby, List<Location> spawnLocations) {
        this.lastAliveType = lastAliveNum;
        this.ready = ready;
        this.name = name;
        this.lobby = lobby;
        if (spawnLocations == null) this.spawnLocations = new ArrayList<>();
        else this.spawnLocations = spawnLocations;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof LastAliveObject && ((LastAliveObject) obj).getLastAliveType() == this.getLastAliveType());
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 37 + lastAliveType + ((ready) ? 1 : 0) + name.hashCode() + lobby.hashCode() + spawnLocations.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "&bName: " + name + "\n&bLobby: " + (lobby != null) + "\n&bSpawn Locations: " + spawnLocations.size();
    }

    public void checkReady() {
        ready = !(name == null || lobby == null || spawnLocations == null || spawnLocations.size() < 2);
    }

    public void setName(String name) {
        this.name = name;
        checkReady();
    }

    public void setLobby(Location location) {
        this.lobby = location;
        checkReady();
    }

    public void addSpawnLocation(Location location) {
        spawnLocations.add(location);
        checkReady();
    }

    public void clearSpawnLocations() {
        spawnLocations.clear();
        checkReady();
    }

}
