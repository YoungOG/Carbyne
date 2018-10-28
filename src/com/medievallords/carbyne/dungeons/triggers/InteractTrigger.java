package com.medievallords.carbyne.dungeons.triggers;

import com.medievallords.carbyne.dungeons.mechanics.Mechanic;
import com.medievallords.carbyne.dungeons.mechanics.data.MechanicData;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class InteractTrigger extends Trigger {

    // 1 = ONCE
    // 2 ONCE PER PERSON
    // 3 UNLIMITED

    private int state;
    private int interacts = 0;
    private List<UUID> playersInteracted = new ArrayList<>();

    public InteractTrigger(String name, Location location, DungeonLineConfig dlc) {
        super(name, location, dlc);

        this.state = dlc.getInt("state", 1);
    }

    public InteractTrigger(String name, Location location, int state) {
        super(name, location, null);

        this.state = state;
    }

    public void trigger(Entity entity) {
        if (state == 1 && interacts >= 1)
            return;
        else if (state == 2 && playersInteracted.contains(entity.getUniqueId()))
            return;

        MechanicData data = new MechanicData(entity, getLocation());
        for (Mechanic mechanic : getMechanics())
            mechanic.runMechanic(data);

        playersInteracted.add(entity.getUniqueId());
        interacts++;
    }
}
