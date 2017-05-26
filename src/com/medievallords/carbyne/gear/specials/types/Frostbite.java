package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-03-18.
 * for the Carbyne project.
 */
public class Frostbite implements Special{

    private int radius = 7;
    @Override
    public int getRequiredCharge() {
        return 50;
    }

    @Override
    public String getSpecialName() {
        return "Frostbite";
    }

    @Override
    public void callSpecial(Player caster) {
        for (Location loc : getBlocksInRadius(caster.getLocation())) {
            double random = Math.random();
            for (Entity entity : caster.getWorld().getNearbyEntities(caster.getLocation(), 30, 30, 30)) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    if (loc.getBlock().getType() == Material.LONG_GRASS || loc.getBlock().getType() == Material.DEAD_BUSH || loc.getBlock().getType() == Material.YELLOW_FLOWER || loc.getBlock().getType() == Material.RED_ROSE || loc.getBlock().getType() == Material.DOUBLE_PLANT) {
                        if (random < 0.5) {
                            player.sendBlockChange(loc, Material.SNOW, (byte) 0);
                        } else {
                            player.sendBlockChange(loc, Material.AIR, (byte) 0);
                        }
                    }
                    else if (loc.getBlock().getType() == Material.CACTUS) {
                        player.sendBlockChange(loc, Material.SNOW_BLOCK, (byte) 0);
                    }
                    else {
                        if (random < 0.6) {
                            player.sendBlockChange(loc, Material.PACKED_ICE, (byte) 0);
                        } else if (random >= 0.6 && random < 0.85) {
                            player.sendBlockChange(loc, Material.SNOW_BLOCK, (byte) 0);
                        } else {
                            continue;
                        }
                    }
                    new BukkitRunnable() {
                        public void run() {
                            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
                        }
                    }.runTaskLater(Carbyne.getInstance(),  200);
                }
            }
            new BukkitRunnable() {
                double times = 0;
                public void run() {
                    if (times > 10) {
                        this.cancel();
                    }
                    ParticleEffect.SNOWBALL.display(0f,0.9f,0f,0.5f,1,loc, 20);
                    times += 0.25;
                }
            }.runTaskTimer(Carbyne.getInstance(), 0, 5);
        }
        new BukkitRunnable() {
            Location loc = caster.getLocation();
            double times = 0;
            public void run() {
                for (Entity entity : caster.getWorld().getNearbyEntities(loc, radius, 5, radius)) {
                    if (entity instanceof LivingEntity && !entity.equals(caster)) {
                        if (entity instanceof Player) {
                            Player hit = (Player) entity;
                            if (isOnSameTeam(caster, hit)) {
                                return;
                            }
                        }
                        LivingEntity livingEntity = (LivingEntity) entity;
                        damageEntity(livingEntity);
                    }
                }
                loc.getWorld().playSound(loc, Sound.GLASS, 3f, (float) Math.random());
                if (times > 10) {
                    this.cancel();
                }
                times += 0.5;
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 10);

        broadcastMessage("&7[&aCarbyne&7]: &5" + caster.getName() + " &ahas casted the &c" + getSpecialName().replace("_", " ") + " &aspecial!", caster.getLocation(), 50);
    }


    private List<Location> getBlocksInRadius(final Location l) {
        final List<Location> blocks = new ArrayList<>();
        for (int x = -this.radius; x <= this.radius; ++x) {
            for (int y = -this.radius; y <= this.radius; ++y) {
                for (int z = -this.radius; z <= this.radius; ++z) {
                    final Location newloc = new Location(l.getWorld(), l.getX() + x, l.getY() + y, l.getZ() + z);
                    if (newloc.getBlock().getType().isOccluding() && newloc.distance(l) <= radius) {
                        blocks.add(newloc);
                    }
                    else if ((newloc.getBlock().getType() == Material.CACTUS || newloc.getBlock().getType() == Material.LONG_GRASS || newloc.getBlock().getType() == Material.DEAD_BUSH || newloc.getBlock().getType() == Material.YELLOW_FLOWER || newloc.getBlock().getType() == Material.RED_ROSE || newloc.getBlock().getType() == Material.DOUBLE_PLANT) && newloc.distance(l) <= radius) {
                        blocks.add(newloc);
                    }
                }
            }
        }
        return blocks;
    }

    public void damageEntity(LivingEntity entity) {
        if (!isInSafeZone(entity)) {
            entity.damage(1);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
        }
    }
}
