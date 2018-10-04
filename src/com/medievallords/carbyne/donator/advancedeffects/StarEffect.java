package com.medievallords.carbyne.donator.advancedeffects;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.donator.AdvancedEffect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.util.DynamicLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class StarEffect extends AdvancedEffect {

    public static final EffectManager effectManager = new EffectManager(Carbyne.getInstance());

    private int theta = 0;
    private final double innerRadius = 2.3, outerRadius = 6;
    private final double startAngle;
    private double cos = 1, sin = 0, thetaRot = 0;
    private Location lo;

    public StarEffect(Player player) {
        super(player);

        this.lo = player.getLocation().clone();

        this.startAngle = Math.toRadians(-18);
    }

    @Override
    public void tick() {
        double angleRad = startAngle + theta * (Math.PI / 5);
        double xA = Math.cos(angleRad);
        double zA = Math.sin(angleRad);
        double x = player.getLocation().getX(), z = player.getLocation().getZ();

        if ((theta & 1) == 0) {
            xA *= outerRadius;
            zA *= outerRadius;
        } else {
            xA *= innerRadius;
            zA *= innerRadius;
        }

//        double newX = (x + xA) * cos + (z + zA) * sin;
//        double newZ = (x + xA) * -sin + (z + zA) * cos;

        if (theta != 0) {
            LineEffect line = new LineEffect(effectManager);
            line.setDynamicOrigin(new DynamicLocation(rotateAroundAxisY(lo.clone().toVector(), cos, sin).toLocation(lo.getWorld())));
            lo.setX(x + xA);
            lo.setZ(z + zA);
            line.setDynamicTarget(new DynamicLocation(rotateAroundAxisY(lo.clone().toVector(), cos, sin).toLocation(lo.getWorld())));
            line.start();
        }

        if (theta >= 10) {
            lo.setX(x + xA);
            lo.setZ(z + zA);
            theta = 0;
            cos = Math.cos(thetaRot);
            sin = Math.sin(thetaRot);
            thetaRot += Math.PI / 100;
        } else {
            theta++;
        }
    }

//    private Vector rotateAroundAxisX(Vector v, double cos, double sin) {
//        double y = v.getY() * cos - v.getZ() * sin;
//        double z = v.getY() * sin + v.getZ() * cos;
//        return v.setY(y).setZ(z);
//    }

    private Vector rotateAroundAxisY(Vector v, double cos, double sin) {
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

//    private Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
//        double x = v.getX() * cos - v.getY() * sin;
//        double y = v.getX() * sin + v.getY() * cos;
//        return v.setX(x).setY(y);
//    }
}
