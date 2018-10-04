package com.medievallords.carbyne.dungeons.mechanics;


import com.medievallords.carbyne.dungeons.mechanics.targeters.Target;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import com.medievallords.carbyne.utils.LocationSerialization;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

@Getter
public class MechanicRedstone extends Mechanic {

    private Location location;

    public MechanicRedstone(String type, DungeonLineConfig lineConfig) {
        super(type, lineConfig);

        this.location = LocationSerialization.deserializeLocation(lineConfig.getString("location", "@w;world:@x;0.0:@y;0.0:@z;0.0:@p;0.0:@ya;0.0"));
    }

    public MechanicRedstone(String type, Target target, Location location) {
        super(type, target);

        this.location = location;
    }

    public boolean cast(World world) {
        Location cloned = new Location(world, location.getX(), location.getY(), location.getZ());
        Block block = cloned.getBlock();
        if (block.getType() != Material.REDSTONE_BLOCK) {
            cloned.getBlock().setType(Material.REDSTONE_BLOCK);
            return true;
        } else {
            block.setType(Material.AIR);
            return true;
        }
    }
}
