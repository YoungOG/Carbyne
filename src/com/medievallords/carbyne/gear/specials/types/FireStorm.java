package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Williams on 2017-03-12
 * for the Carbyne project.
 */
public class FireStorm implements Special{

    private double damagePerRound = 5;
    private Firework fireworkTo;

    @Override
    public int getRequiredCharge() {
        return 50;
    }

    @Override
    public String getSpecialName() {
        return "Fire_Storm";
    }

    @Override
    public void callSpecial(Player caster) {
        Location centerLocation = caster.getLocation();

        new BukkitRunnable() {
            double t = 0;
            int times = 0;
            double radius = 1.0;
            Location copy = centerLocation.clone();
            @Override
            public void run() {
                t = t + 0.35;
                if (times == 20 || times == 40 || times == 60) {
                    for (Entity entity : copy.getWorld().getNearbyEntities(copy, radius, radius, radius)) {
                        if (entity instanceof LivingEntity && !entity.equals(caster)) {
                            damageEntity((LivingEntity) entity, caster);
                        }
                    }
                    copy.getWorld().playSound(copy, Sound.FIREWORK_BLAST2, 3.0f, 0.8f);
                }
                if (radius < 3 && radius > 0) {
                    for (int i = 0; i < 360; i += 5) {
                        double x = Math.cos(i) * radius;
                        double y = 1;
                        double z = Math.sin(i) * radius;
                        copy.add(x, y, z);
                        ParticleEffect.LAVA.display(0f, 0f, 0f, 0f ,1, copy, 30);
                        copy.subtract(x, y, z);
                    }
                }

                else if (radius < 6 && radius >= 3) {
                    for (int i = 0; i < 360; i += 3.5) {
                        double x = Math.cos(i) * radius;
                        double y = 1;
                        double z = Math.sin(i) * radius;
                        copy.add(x, y, z);
                        ParticleEffect.LAVA.display(0f, 0f, 0f, 0f ,1, copy, 30);
                        copy.subtract(x, y, z);
                    }
                }

                else if (radius < 10 && radius >= 6) {
                    for (int i = 0; i < 360; i += 1) {
                        double x = Math.cos(i) * radius;
                        double y = 1;
                        double z = Math.sin(i) * radius;
                        copy.add(x, y, z);
                        ParticleEffect.LAVA.display(0f, 0f, 0f, 0f ,1, copy, 30);
                        copy.subtract(x, y, z);
                    }
                }

                times++;
                radius += 0.2;

                if (times > 60) {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 1);

        broadcastMessage("&7[&aCarbyne&7]: &5" + caster.getName() + " &ahas casted the &c" + getSpecialName().replace("_", " ") + " &aspecial!", caster.getLocation(), 50);
    }

    public void damageEntity(LivingEntity entity, Player caster) {
        //EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(caster, entity, DamageCause.ENTITY_ATTACK, damagePerRound);
        //Bukkit.getServer().getPluginManager().callEvent(damageEvent);
        if (!isInSafeZone(entity)) {
            entity.damage(damagePerRound);
        }
        //entity.setFireTicks(20 * 5);
    }

}
