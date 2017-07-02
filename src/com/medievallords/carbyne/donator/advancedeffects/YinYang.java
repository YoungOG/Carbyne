package com.medievallords.carbyne.donator.advancedeffects;

import com.medievallords.carbyne.donator.AdvancedEffect;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by Dalton on 6/28/2017.
 */
public class YinYang extends AdvancedEffect
{

    private static final double RADIUS = 1.85;
    private static final ParticleEffect.OrdinaryColor black = new ParticleEffect.OrdinaryColor(0, 0, 0);

    private double superTheta = 0;

    public YinYang(Player player)
    {
        super(player);
    }

    public void tick() {
        superTheta += 0.076;

        Location location = player.getLocation().add(0, 0.2, 0);

        double x = (Math.sin(superTheta));
        double z = (Math.cos(superTheta));

        ParticleEffect.FIREWORKS_SPARK.display(new Vector(x, 0, z), 0.4f, location, 50, false);
        ParticleEffect.FIREWORKS_SPARK.display(new Vector(-x, 0, -z), 0.4f, location, 50, false);

        x = (Math.sin(superTheta - (RADIUS + 0.05)))* RADIUS;
        z = (Math.cos(superTheta - (RADIUS + 0.05)))* RADIUS;

        location.add(x,0,z);
        ParticleEffect.SMOKE_NORMAL.display(0,0,0,0, 1, location, 50, false);
        location.subtract(x,0,z);
        location.subtract(x,0,z);
        ParticleEffect.SMOKE_NORMAL.display(0,0,0,0, 1, location, 50, false);
    }
}