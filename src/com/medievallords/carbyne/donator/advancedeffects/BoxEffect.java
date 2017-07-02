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

    private static double LWH = 3; // length, width, height

    @Override
    public void tick()
    {

        Location location = player.getLocation();

        location.subtract(LWH/2, 0, LWH/2);

        double interval = LWH/8;

        double minx = location.getX(), maxx = location.getX() + LWH,
                minz = location.getZ(), maxz = location.getZ() + LWH,
                miny = location.getY(), maxy = location.getY() + LWH;

        for(double x = 0; x < LWH; x += LWH) {
            for (double z = 0; z <= LWH; z += LWH) {
                for (double y = 0; y < LWH; y += LWH) {
                    location.add(x, y, z);
                    if(location.getX() >= minx && location.getX() <= maxx && location.getZ() >= minz && location.getZ() <= maxz && location.getY() >= miny && location.getY() <= maxy)
                        ParticleEffect.REDSTONE.display(0, 0, 0, 0.02f, 1, location, 50, false);
                    location.subtract(x,y,z);
                }
            }
        }

    }

}
