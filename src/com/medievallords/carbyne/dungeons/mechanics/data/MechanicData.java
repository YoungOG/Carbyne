package com.medievallords.carbyne.dungeons.mechanics.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MechanicData {

    private Location location;
    private Entity trigger;

    private List<Location> targetLocations = new ArrayList<>();
    private List<Entity> targetEntities = new ArrayList<>();

    public MechanicData(Entity trigger, Location location) {
        this.location = location;
        this.trigger = trigger;
    }
}
