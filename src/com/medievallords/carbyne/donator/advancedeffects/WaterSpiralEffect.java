package com.medievallords.carbyne.donator.advancedeffects;

import com.medievallords.carbyne.donator.AdvancedEffect;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-06-29
 * for the Carbyne project.
 */
public class WaterSpiralEffect extends AdvancedEffect {

    private double range = 2;
    private double theta;

    public WaterSpiralEffect(Player player) {
        super(player);
    }

    @Override
    public void tick() {
        if (player.isSprinting() || (player.getVelocity().getX() > 0 || player.getVelocity().getZ() > 0)) {
            ParticleEffect.DRIP_WATER.display(0,0,0,0, 3, player.getLocation(), 50, false);
            return;
        }

        theta += 0.07;
        double x = Math.sin(theta);
        double y = 2.05;
        double z = Math.cos(theta);
        Location location = player.getLocation().clone();
        location.add(0,y,0);

        for (double i = 0.1; i < range; i +=0.1) {
            location.add(x * i,0,z * i);
            ParticleEffect.DRIP_WATER.display(0,0,0,0,1,location,40,false);
            location.subtract(x * i,0,z * i);
        }
    }
}
