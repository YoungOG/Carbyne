package com.medievallords.carbyne.events.implementations.object;

import lombok.Getter;
import org.bukkit.Location;

public abstract class RaceObject {

    private static int raceNum = 0;

    @Getter
    private final int raceType;
    @Getter
    private final String name, startString;
    @Getter
    private final Location startingLocation, winningLocation;

    public RaceObject(String name, String startString, Location startingLocation, Location winningLocation) {
        raceType = raceNum;
        raceNum++;
        this.name = name;
        this.startString = startString;
        this.startingLocation = startingLocation;
        this.winningLocation = winningLocation;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RaceObject && ((RaceObject) other).getRaceType() == this.raceType;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 37 + raceType + name.hashCode() + startString.hashCode() + startingLocation.hashCode() + winningLocation.hashCode();
        return hash;
    }

}
