package com.medievallords.carbyne.dungeons.triggers;

import com.medievallords.carbyne.dungeons.mechanics.Mechanic;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Trigger {

    public List<Mechanic> mechanics = new ArrayList<>();

    private String name;
    private Location location;

    public Trigger(String name, Location location, DungeonLineConfig dlc) {
        this.name = name;
        this.location = location;
    }

    public static Trigger getTrigger(String name, String type, Location location, DungeonLineConfig dlc) {
        switch (type.toLowerCase()) {
            case "distance":
                return new DistanceTrigger(name, location, dlc);
            case "interact":
                return new InteractTrigger(name, location, dlc);
            case "mobdeath":
                return new MobTrigger(name, location, dlc);
        }

        return null;
    }

    public Mechanic getMechanic(String name) {
        for (Mechanic mechanic : mechanics)
            if (mechanic.getType().equalsIgnoreCase(name))
                return mechanic;
        return null;
    }
}
