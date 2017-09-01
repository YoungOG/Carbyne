package com.medievallords.carbyne.events.implementations.object;

import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class RaceObject {

    private static int raceNum = 0;

    public static List<RaceObject> raceObjects = new ArrayList<>();

    public static RaceObject getRaceObject(String name) {
        String compare = name;
        for (RaceObject raceObject : raceObjects) {
            String currentCompare = MessageManager.stripStringOfAmpersandColors(raceObject.getName());
            if (compare.equalsIgnoreCase(currentCompare)) return raceObject;
        }
        return null;
    }

    @Getter
    private boolean ready;
    @Getter
    private final int raceType;
    @Getter
    private String name, startString;
    @Getter
    private Location startingLocation, winningLocation, gateLocation;

    public RaceObject(boolean ready, String name, String startString, Location startingLocation, Location winningLocation, Location gateLocation) {
        this.ready = ready;
        raceType = raceNum;
        raceNum++;
        this.name = name;
        this.startString = startString;
        this.startingLocation = startingLocation;
        this.winningLocation = winningLocation;
        this.gateLocation = gateLocation;
    }

    public void checkReady() {
        ready = !(name == null || startString == null || startingLocation == null || winningLocation == null || gateLocation == null);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RaceObject && ((RaceObject) other).getRaceType() == this.raceType;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 37 + ((ready) ? 1 : 0) + raceType + name.hashCode() + startString.hashCode() + startingLocation.hashCode() + winningLocation.hashCode() + gateLocation.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "Name: " + name + "&r\nStartMessage: " + startString + "\n&rStartingLocation: " + (startingLocation != null) +
                "\n&rWinningLocation: " + (winningLocation != null) + "\n&rGateLocation: " + (gateLocation != null);
    }

    public void setName(String name) {
        this.name = name;
        checkReady();
    }

    public void setStartString(String startString) {
        this.startString = startString;
        checkReady();
    }

    public void setGateLocation(Location gateLocation) {
        this.gateLocation = gateLocation;
        checkReady();
    }

    public void setWinningLocation(Location winningLocation) {

        this.winningLocation = winningLocation;
        checkReady();
    }

    public void setStartingLocation(Location startingLocation) {

        this.startingLocation = startingLocation;
        checkReady();
    }
}
