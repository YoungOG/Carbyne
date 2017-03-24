package com.medievallords.carbyne.regeneration;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

/**
 * Created by Calvin on 3/24/2017
 * for the Carbyne project.
 */

@Getter
@Setter
public class BlockRegenerationData implements Cloneable {

    private Material previousMaterial, newMaterial;
//    private short previousData, newData;
    private int regenerationTime;

    public BlockRegenerationData(Material previousMaterial, Material newMaterial, int regenerationTime) {
        this.previousMaterial = previousMaterial;
        this.newMaterial = newMaterial;
        this.regenerationTime = regenerationTime;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
