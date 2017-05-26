package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.utils.ParticleEffect;
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

/**
 * Created by Williams on 2017-03-16.
 * for the Carbyne project.
 */
public class HinderingShot implements Special {

    @Override
    public int getRequiredCharge() {
        return 50;
    }

    @Override
    public String getSpecialName() {
        return "Hindering_Shot";
    }


    @Override
    public void callSpecial(Player caster) {
        new BukkitRunnable() {

            Location loc = caster.getEyeLocation();
            Vector vector = loc.getDirection().normalize();
            double t = 0;
            @Override
            public void run() {
                t++;
                double x = vector.getX() * t;
                double y = vector.getY() * t;
                double z = vector.getZ() * t;
                loc.add(x, y, z);

                if (t > 175 || loc.getBlock().getType() != Material.AIR) {
                    this.cancel();
                    loc.getWorld().playEffect(loc, Effect.EXPLOSION_HUGE, 1);
                    ParticleEffect.LAVA.display(0f, 0f, 0f, 1f, 5, loc, 30);
                    for (Entity entity : loc.getWorld().getNearbyEntities(loc, 5, 5, 5)) {
                        if (entity instanceof LivingEntity && !entity.equals(caster)) {
                            damageEntity((LivingEntity) entity, caster, 7);
                        }
                    }
                }

                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1.4, 1.4, 1.4)) {
                    if (entity instanceof LivingEntity && !entity.equals(caster)) {
                        damageEntity((LivingEntity) entity, caster, 10);
                    }
                }

                ParticleEffect.FLAME.display(0f, 0f, 0f, 0f, 2, loc, 40);
                loc.subtract(x, y, z);
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 1);

        broadcastMessage("&7[&aCarbyne&7]: &5" + caster.getName() + " &ahas casted the &c" + getSpecialName().replace("_", " ") + " &aspecial!", caster.getLocation(), 50);
    }

    public void damageEntity(LivingEntity entity, Player caster, double damage) {
        //EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(caster, entity, DamageCause.ENTITY_ATTACK, damagePerRound);
        //Bukkit.getServer().getPluginManager().callEvent(damageEvent);
        if (!isInSafeZone(entity)) {
            entity.damage(damage);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1));
            entity.setFireTicks(20 * 5);
        }
    }

}
