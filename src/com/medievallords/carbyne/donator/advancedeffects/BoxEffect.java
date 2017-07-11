package com.medievallords.carbyne.donator.advancedeffects;

import com.medievallords.carbyne.donator.AdvancedEffect;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Dalton on 6/30/2017.
 */
public class BoxEffect extends AdvancedEffect
{

    public BoxEffect(Player player)
    {
        super(player);
    }

    private static double LWH = 2.15; // length, width, height
    private double rotation;

    @Override
    public void tick()
    {
        chooseColor();
        rotation += 0.01;
        Location location = player.getLocation().clone();

        //location.subtract(LWH/2, 0, LWH/2);

        double interval = (LWH / 2);

        for (double x = -(LWH / 2); x <= LWH / 2; x += interval) {
            for (double y = 0; y <= LWH; y += interval) {
                for (double z = 0; z <= LWH / 2; z += interval) {
                    double x_new = x * Math.cos(rotation) - z * Math.sin(rotation);
                    double z_new = x * Math.sin(rotation) + z * Math.cos(rotation);
                    location.add(x_new, y, z_new);
                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(r, g, b), location, 50, true);
                    location.subtract(x_new, 0, z_new);
                    location.subtract(x_new, 0, z_new);
                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(r, g, b), location, 50, true);
                    location.add(x_new, -y, z_new);
                }
            }
        }

    }

    private int state = 0;
    private int a = 255;
    private int r = 255;
    private int g = 0;
    private int b = 0;

    public void chooseColor() {
        if(state == 0){
            g+=5;
            if(g == 255)
                state = 1;
        }
        if(state == 1){
            r-=5;
            if(r == 0)
                state = 2;
        }
        if(state == 2){
            b+=5;
            if(b == 255)
                state = 3;
        }
        if(state == 3){
            g-=5;
            if(g == 0)
                state = 4;
        }
        if(state == 4){
            r+=5;
            if(r == 255)
                state = 5;
        }
        if(state == 5){
            b-=5;
            if(b == 0)
                state = 0;
        }
        int hex = (a << 24) + (r << 16) + (g << 8) + (b);
    }

}
