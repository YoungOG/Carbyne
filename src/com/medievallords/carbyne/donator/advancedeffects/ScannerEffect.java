package com.medievallords.carbyne.donator.advancedeffects;

import com.medievallords.carbyne.donator.AdvancedEffect;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-06-29
 * for the Carbyne project.
 */
public class ScannerEffect extends AdvancedEffect{

    private double radius = 1.1;
    private double height;

    public ScannerEffect(Player player) {
        super(player);
    }


    @Override
    public void tick() {
        height += 0.065;

        for (double theta = 0; theta < 16; theta +=0.1) {
            double x = Math.sin(theta) * radius;
            double y = Math.cos(height) + 1;
            double z = Math.cos(theta) * radius;
            Location location = player.getLocation().clone();
            location.add(x,y,z);

            ParticleEffect.PORTAL.display(0,0,0,0,1, location, 30, false);
            location.subtract(x,y,z);
        }
    }
}
