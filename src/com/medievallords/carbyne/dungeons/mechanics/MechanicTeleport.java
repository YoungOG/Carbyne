package com.medievallords.carbyne.dungeons.mechanics;

import com.medievallords.carbyne.dungeons.mechanics.targeters.Target;
import com.medievallords.carbyne.dungeons.mechanics.targeters.instances.ITargetEntity;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import com.medievallords.carbyne.utils.LocationSerialization;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
@Getter
public class MechanicTeleport extends Mechanic implements ITargetEntity {

    private Location location;

    public MechanicTeleport(String type, DungeonLineConfig lineConfig) {
        super(type, lineConfig);

        location = LocationSerialization.deserializeLocation(lineConfig.getString("location", "@w;world:@x;0.0:@y;0.0:@z;0.0:@p;0.0:@ya;0.0"));
    }

    public MechanicTeleport(String type, Target target, Location location) {
        super(type, target);

        this.location = location;
    }

    @Override
    public boolean cast(Entity entity) {
        Location to = location.clone();
        to.setWorld(entity.getWorld());
        entity.teleport(to);
        return true;
    }
}
