package com.medievallords.carbyne.heartbeat;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

@Setter
@Getter
public class BlockType {

    private Material material;
    private Location location;

    public BlockType(Material material, Location location) {
        this.location = location;
        this.material = material;
    }
}