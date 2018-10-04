package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.utils.Maths;
import com.medievallords.carbyne.utils.ParticleEffect;
import com.medievallords.carbyne.utils.PlayerHealth;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FrostBolt implements Special {

    @Override
    public int getRequiredCharge() {
        return 70;
    }

    @Override
    public String getSpecialName() {
        return "Frost_Bolt";
    }


    @Override
    public void callSpecial(final Player caster) {
        new BukkitRunnable() {
            final Location loc = caster.getEyeLocation().clone();
            final Vector vector = loc.getDirection().normalize();
            double t = 0;
            boolean cancel = false;
            @Override
            public void run() {
                t++;
                final double x = vector.getX() * t;
                final double y = vector.getY() * t;
                final double z = vector.getZ() * t;
                loc.add(x, y, z);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (t > 175 || loc.getBlock().getType().isSolid()) {
                            cancel = true;

                            for (final Location otherLoc : getBlocksInRadius(loc, 7)) {
                                final double random = Math.random();
                                for (final Entity entity : caster.getWorld().getNearbyEntities(loc, 30, 30, 30)) {
                                    if (entity instanceof Player) {
                                        final Player player = (Player) entity;
                                        if (random < 0.6) {
                                            player.sendBlockChange(otherLoc, Material.PACKED_ICE, (byte) 0);
                                        } else if (random >= 0.6 && random < 0.85) {
                                            player.sendBlockChange(otherLoc, Material.SNOW_BLOCK, (byte) 0);
                                        } else {
                                            continue;
                                        }
                                        new BukkitRunnable() {
                                            public void run() {
                                                player.sendBlockChange(otherLoc, otherLoc.getBlock().getType(), otherLoc.getBlock().getData());
                                            }
                                        }.runTaskLaterAsynchronously(Carbyne.getInstance(),  200);
                                    }
                                }
                                new BukkitRunnable() {
                                    double times = 0;
                                    public void run() {
                                        if (times > 9) {
                                            this.cancel();
                                        }
                                        ParticleEffect.SNOWBALL.display(0f,0.9f,0f,0.5f,1, otherLoc, 20, false);
                                        times += 0.25;
                                    }
                                }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 5);
                            }


                            ParticleEffect.SNOWBALL.display(7,
                                    5,
                                    7, 1f, 40, loc, 70, false);
                            for (final Entity entity : loc.getWorld().getNearbyEntities(loc, 7, 5, 7)) {
                                if (entity instanceof LivingEntity && !entity.equals(caster)) {
                                    LivingEntity livingEntity = (LivingEntity) entity;
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, 1000));
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 10));
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 10));
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 140, 2));
                                }
                            }
                        }
                    }
                }.runTask(Carbyne.getInstance());

//                for (final Entity entity : loc.getWorld().getNearbyEntities(loc, 1.4, 1.4, 1.4)) {
//                    if (entity instanceof LivingEntity && !entity.equals(caster)) {
//                        damageEntity((LivingEntity) entity, 10);
//                    }
//                }

                if (cancel) {
                    this.cancel();
                    return;
                }

                Location l = loc.clone();
                for(double phi = 0; phi < Math.PI; phi += Math.PI / 4) {
                    for(double theta = 0; theta < Math.PI * 2; theta += Math.PI / 8) {
                        double x2 = Math.cos(theta) * Math.sin(phi);
                        double y2 = Math.cos(phi) + 1.5;
                        double z2 = Math.sin(theta) * Math.sin(phi);

                        l.add(x2,y2,z2);
                        ParticleEffect.SNOWBALL.display(Maths.randomNumberBetween(2, 0) - 1,
                                Maths.randomNumberBetween(2, 0) - 1,
                                Maths.randomNumberBetween(2, 0) - 1, 1f, 1, l, 70, false);
                        l.subtract(x2, y2, z2);
                    }
                }

                loc.subtract(x, y, z);
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 1);

        broadcastMessage("&7[&aCarbyne&7]: &5" + caster.getName() + " &ahas casted the &c" + getSpecialName().replace("_", " ") + " &aspecial!", caster.getLocation(), 50);
    }

    private List<Location> getBlocksInRadius(final Location l, int radius) {
        final List<Location> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    final Location newloc = new Location(l.getWorld(), l.getX() + x, l.getY() + y, l.getZ() + z);
                    if (newloc.distance(l) <= radius) {
                        if (newloc.getBlock().getType().isSolid()) {
                            blocks.add(newloc);
                        }
                    }
                }
            }
        }
        return blocks;
    }
}
