package com.medievallords.carbyne.dungeons.mechanics.targeters;


import com.medievallords.carbyne.dungeons.mechanics.data.MechanicData;
import org.bukkit.Location;

public class LocationTarget extends Target {

    public LocationTarget(String params, String type) {
        super(params, type);
    }

    public Location getLocation(MechanicData data) {
        return data.getLocation();
    }
}
