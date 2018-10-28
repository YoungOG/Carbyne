package com.medievallords.carbyne.dungeons.mechanics;

import com.medievallords.carbyne.dungeons.mechanics.data.MechanicData;
import com.medievallords.carbyne.dungeons.mechanics.targeters.LocationTarget;
import com.medievallords.carbyne.dungeons.mechanics.targeters.PIRTarget;
import com.medievallords.carbyne.dungeons.mechanics.targeters.PlayerTarget;
import com.medievallords.carbyne.dungeons.mechanics.targeters.Target;
import com.medievallords.carbyne.dungeons.mechanics.targeters.instances.ITargetEntity;
import com.medievallords.carbyne.dungeons.mechanics.targeters.instances.ITargetLocation;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class Mechanic {

    @Getter
    private Target target;
    @Getter
    private String type;

    public Mechanic(String type, DungeonLineConfig lineConfig) {
        this.type = type;
        String targetName = lineConfig.getString("target", "LT");
        this.target = Target.getTarget(targetName);
    }

    public Mechanic(String type, Target target) {
        this.type = type;
        this.target = target;
    }

    public static Mechanic getMechanic(String name, List<String> data) {
        switch (name.toLowerCase()) {
            case "dropitem":
                return new MechanicDropItem(name.toLowerCase(), new DungeonLineConfig(data));
            case "message":
                return new MechanicMessage(name.toLowerCase(), new DungeonLineConfig(data));
            case "spawnentity":
                return new MechanicSpawnEntity(name.toLowerCase(), new DungeonLineConfig(data));
            case "redstone":
                return new MechanicRedstone(name.toLowerCase(), new DungeonLineConfig(data));
            case "teleport":
                return new MechanicTeleport(name.toLowerCase(), new DungeonLineConfig(data));
        }

        return null;
    }

    public void runMechanic(MechanicData data) {
        if (this instanceof MechanicRedstone)
            ((MechanicRedstone) this).cast(data.getLocation().getWorld());

        if (target instanceof LocationTarget)
            data.getTargetLocations().add(((LocationTarget) target).getLocation(data));
        else if (target instanceof PIRTarget) {
            List<Player> players = ((PIRTarget) target).getPlayers(data);
            data.getTargetEntities().addAll(players);
        }

        if (this instanceof ITargetLocation) {
            if (target instanceof LocationTarget)
                ((ITargetLocation) this).cast(data.getTargetLocations().get(0));
        } else if (this instanceof ITargetEntity) {
            if (target instanceof PIRTarget) {
                for (Entity entity : data.getTargetEntities())
                    ((ITargetEntity) this).cast(entity);
            } else if (target instanceof PlayerTarget) {
                ((ITargetEntity) this).cast(data.getTrigger());
            }
        }
    }
}
