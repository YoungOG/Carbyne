package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.utils.InstantFirework;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-03-12.
 */
public class WitherStorm implements Special {

    private double damagePerRound = 6;
    private final FireworkEffect[] effects;

    public WitherStorm() {
        this.effects = getFireworkEffect();
    }

    @Override
    public int getRequiredCharge() {
        return 50;
    }

    @Override
    public String getSpecialName() {
        return "Wither_Storm";
    }

    @Override
    public void callSpecial(final Player caster) {
        final Location centerLocation = caster.getLocation();
        new BukkitRunnable() {
            private double t = 0;
            private double times = 0;
            private double radius = 8;

            @Override
            public void run() {
                t = t + 0.35;
                final double x = Math.sin(t) + Math.sin(t) * radius;
                final double y = t - t + 1;
                final double z = Math.cos(t) + Math.cos(t) * radius;
                final List<Player> players = new ArrayList<>();
                if (times == 20 || times == 40 || times == 60) {
                    for (final Entity entity : centerLocation.getWorld().getNearbyEntities(centerLocation, radius, radius, radius)) {
                        if (entity instanceof LivingEntity && !entity.equals(caster)) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    damageEntity((LivingEntity) entity);
                                }
                            }.runTask(Carbyne.getInstance());
                        }

                        if (entity.getType() == EntityType.PLAYER) {
                            players.add((Player) entity);
                        }
                    }
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        centerLocation.add(x, y, z);
                        InstantFirework.spawn(centerLocation, players, effects);
                        centerLocation.subtract(x, y, z);
                    }
                }.runTask(Carbyne.getInstance());

                times++;
                radius -= 0.2;
                if (times > 60) {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 1);

        broadcastMessage("&7[&aCarbyne&7]: &5" + caster.getName() + " &ahas casted the &c" + getSpecialName().replace("_", " ") + " &aspecial!", caster.getLocation(), 50);
    }

    private void damageEntity(final LivingEntity entity) {
        if (!isInSafeZone(entity)) {
            if (entity instanceof Player) {
                double health = entity.getHealth();
                double damage = health - (health * 0.67);
                entity.damage(damage);
                entity.setFireTicks(20 * 5);
                entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
                return;
            }

            entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
            entity.damage(damagePerRound);
            entity.setFireTicks(20 * 5);
        }
    }

    public FireworkEffect[] getFireworkEffect() {
        FireworkEffect effect = FireworkEffect.builder().flicker(false).trail(false).withColor(Color.BLACK).withFade(Color.GRAY).with(FireworkEffect.Type.BURST).build();
        FireworkEffect effect1 = FireworkEffect.builder().trail(false).flicker(false).withColor(Color.GRAY).withFade(Color.GRAY).with(FireworkEffect.Type.BURST).build();
        return new FireworkEffect[]{
                effect, effect1
        };
    }
}
