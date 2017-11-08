package com.medievallords.carbyne.mechanics;

import com.medievallords.carbyne.Carbyne;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;


/**
 * Created by WE on 2017-08-19.
 */
public class StormMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private int r, r2, g, g2, b, b2;
    private double radius;
    private int times;
    private String type;

    public StormMechanic(String skill, MythicLineConfig mlc, int interval) {
        super(skill, mlc, interval);

        String color1 = mlc.getString("color1");
        String color2 = mlc.getString("color2");
        String[] split = color1.split(",");
        String[] split2 = color1.split(",");
        try {
            r = Integer.parseInt(split[0]);
            r2 = Integer.parseInt(split[1]);
            g = Integer.parseInt(split[2]);
            g2 = Integer.parseInt(split2[0]);
            b = Integer.parseInt(split2[1]);
            b2 = Integer.parseInt(split2[2]);
        } catch (NumberFormatException e) {

        }

        radius = mlc.getDouble("radius");
        type = mlc.getString("type");
        times = mlc.getInteger("times");
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        callMechanic(abstractEntity.getBukkitEntity().getLocation());
        return false;
    }

    public void callMechanic(Location location) {
        Location centerLocation = location.clone();
        new BukkitRunnable() {

            double t = 0;
            int ran = 0;
            double radius = 10;

            @Override
            public void run() {

                t = t + 0.35;
                double x = Math.sin(t) + Math.sin(t) * radius;
                double y = t - t + 1;
                double z = Math.cos(t) + Math.cos(t) * radius;

                centerLocation.add(x, y, z);
                runFireworks(centerLocation);
                centerLocation.subtract(x, y, z);

                ran++;
                if (ran > times) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 1);

    }

    public void runFireworks(Location location) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        org.bukkit.FireworkEffect effect = org.bukkit.FireworkEffect.builder().with(FireworkEffect.Type.valueOf(type)).withColor(Color.fromRGB(r, g, b)).withColor(Color.fromRGB(r2, g2, b2)).build();
        FireworkMeta meta = firework.getFireworkMeta();
        meta.clearEffects();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
        new BukkitRunnable() {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(Carbyne.getInstance(), 2);
    }
}
