package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-08-12
 * for the Carbyne project.
 */
public class InfernalExplosion implements Special {

    @Override
    public int getRequiredCharge() {
        return 60;
    }

    @Override
    public String getSpecialName() {
        return "Infernal_Explosion";
    }

    @Override
    public void callSpecial(final Player caster) {

        caster.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        caster.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

        caster.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 2));
        caster.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 2));

        final List<Color> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.ORANGE);
        runFireworks(caster.getLocation(), FireworkEffect.Type.BALL_LARGE, colors);
        runFireworks(caster.getLocation(), FireworkEffect.Type.BALL_LARGE, colors);
        runFireworks(caster.getLocation(), FireworkEffect.Type.BALL_LARGE, colors);
        ParticleEffect.FLAME.display(1, 1, 1, 3, 50, caster.getLocation(), 100, false);
        ParticleEffect.LAVA.display(1, 1, 1, 3, 50, caster.getLocation(), 100, false);

        for (final Entity entity : caster.getNearbyEntities(5, 5, 5)) {
            if (!(entity instanceof LivingEntity)) {
                return;
            }

            if (isInSafeZone((LivingEntity) entity)) {
                return;
            }

            colors.clear();
            colors.add(Color.YELLOW);
            colors.add(Color.ORANGE);
            runFireworks(caster.getLocation(), FireworkEffect.Type.BURST, colors);
            entity.setFireTicks(500);

            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (!isOnSameTeam(caster, player)) {
                    Vector vector = (caster.getLocation().toVector().subtract(player.getLocation().toVector())).normalize().multiply(4);
                    player.setVelocity(new Vector(vector.getX(), 2, vector.getZ()));
                }
            } else {
                Vector vector = (caster.getLocation().toVector().subtract(entity.getLocation().toVector())).normalize().multiply(4);
                entity.setVelocity(new Vector(vector.getX(), 2, vector.getZ()));
            }
        }
    }

    public void runFireworks(final Location location, final FireworkEffect.Type type, final List<Color> colors) {
        final Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        final org.bukkit.FireworkEffect effect = org.bukkit.FireworkEffect.builder().with(type).withColor(colors).build();
        final FireworkMeta meta = firework.getFireworkMeta();
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
