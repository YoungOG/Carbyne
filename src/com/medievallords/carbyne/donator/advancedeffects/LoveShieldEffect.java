package com.medievallords.carbyne.donator.advancedeffects;

import com.medievallords.carbyne.donator.AdvancedEffect;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-06-28
 * for the Carbyne project.
 */
public class LoveShieldEffect extends AdvancedEffect {

    private float height = 2.04f;
    private float radius = 1;
    private double theta;

    public LoveShieldEffect(Player player) {
        super(player);
    }

    @Override
    public void tick() {
        if (player.isSprinting() || (player.getVelocity().getX() > 0 || player.getVelocity().getZ() > 0)) {
            ParticleEffect.HEART.display(0,0,0,0, 1, player.getLocation(), 50, false);
            return;
        }

        theta += 0.045;

        double x = Math.sin(theta) * radius;
        double y = 0.05;
        double z = Math.cos(theta) * radius;

        Location location = player.getLocation().clone();
        location.add(x, y, z);
        for (double i = 0.1; i < height; i += 0.35) {
            location.add(0, i, 0);
            ParticleEffect.HEART.display(0,0,0,0, 1, location, 50, false);
            location.subtract(0, i, 0);
        }

        location.subtract(x, y, z);
        location.subtract(x, y, z);

        for (double i = 0; i < height; i += 0.35) {
            location.add(0, i, 0);
            ParticleEffect.HEART.display(0,0,0,0, 1, location, 50, false);
            location.subtract(0, i, 0);
        }
    }
}
