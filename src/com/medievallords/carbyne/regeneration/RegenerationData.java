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
public class RegenerationData implements Cloneable {

    private Material previousMaterial, newMaterial;
//    private short previousData, newData;
    private String regenerationTimeString;

    public RegenerationData(Material previousMaterial, Material newMaterial, String regenerationTimeString) {
        this.previousMaterial = previousMaterial;
        this.newMaterial = newMaterial;
        this.regenerationTimeString = regenerationTimeString;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
