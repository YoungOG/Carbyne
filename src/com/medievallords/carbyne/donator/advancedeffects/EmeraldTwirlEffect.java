package com.medievallords.carbyne.donator.advancedeffects;

import com.medievallords.carbyne.donator.AdvancedEffect;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-06-29
 * for the Carbyne project.
 */
public class EmeraldTwirlEffect extends AdvancedEffect {

    private double theta;
    private double radius = 1.08;

    public EmeraldTwirlEffect(Player player) {
        super(player);
    }

    @Override
    public void tick() {
        theta += 0.08;
        double x = Math.sin(theta) * radius;
        double y = Math.cos(theta) + 1;
        double z = Math.cos(theta) * radius;

        Location location = player.getLocation().clone();
        location.add(x,y,z);
        ParticleEffect.VILLAGER_HAPPY.display(0,0,0,0,1,location, 40, false);
        location.subtract(x,0,z);
        location.subtract(x,0,z);
        ParticleEffect.VILLAGER_HAPPY.display(0,0,0,0,1,location, 40, false);
        location.add(x,0,z);
    }
}
