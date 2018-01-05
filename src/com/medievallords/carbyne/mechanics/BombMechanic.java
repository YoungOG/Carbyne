package com.medievallords.carbyne.mechanics;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.ParticleEffect;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Williams on 2017-09-05
 * for the Carbyne project.
 */
public class BombMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {

    private FireworkEffect.Type type;
    private int r, g, b;
    private int tick;
    private int duration;
    private double damage, radius;

    public BombMechanic(String skill, MythicLineConfig mlc, int interval) {
        super(skill, mlc, interval);

        String[] colors = mlc.getString("color").split(",");
        try {
            this.r = Integer.parseInt(colors[0]);
            this.g = Integer.parseInt(colors[1]);
            this.b = Integer.parseInt(colors[2]);
        } catch (NumberFormatException e) {

        }
        this.type = FireworkEffect.Type.valueOf(mlc.getString("type"));
        this.tick = mlc.getInteger("tick");
        this.duration = mlc.getInteger("duration");
        this.damage = mlc.getDouble("damage");
        this.radius = mlc.getDouble("radius");
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        final double end = (20 / tick) * duration;
        new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                time++;
                Location location = abstractEntity.getBukkitEntity().getLocation();
                if (time > end) {
                    explode(location, damage, radius);
                    cancel();
                    return;
                }

                runFireworks(location);
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, tick);
        return false;
    }

    @Override
    public boolean castAtLocation(SkillMetadata skillMetadata, final AbstractLocation abstractLocation) {
        final double end = (20 / tick) * duration;
        final Location location = BukkitAdapter.adapt(abstractLocation);
        new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                time++;
                if (time > end) {
                    explode(location, damage, radius);
                    cancel();
                    return;
                }

                runFireworks(location);
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, tick);
        return false;
    }

    private void runFireworks(final Location location) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        org.bukkit.FireworkEffect effect = org.bukkit.FireworkEffect.builder().with(type).withColor(org.bukkit.Color.fromRGB(r, g, b)).withColor().build();
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

    private void explode(final Location location, final double damage, final double radius) {
        ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 1, location, 40, true);
        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(damage);
            }
        }
    }
}
