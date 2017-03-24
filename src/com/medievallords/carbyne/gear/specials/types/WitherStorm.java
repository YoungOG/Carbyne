package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Williams on 2017-03-12.
 */
public class WitherStorm implements Special{

    private double damagePerRound = 5;
    private Firework fireworkTo;

    @Override
    public int getRequiredCharge() {
        return 50;
    }

    @Override
    public String getSpecialName() {
        return "Wither_Storm";
    }

    @Override
    public void callSpecial(Player caster) {
        Location centerLocation = caster.getLocation();
        new BukkitRunnable() {

            double t = 0;
            double times = 0;
            double radius = 10;

            @Override
            public void run() {
                if (fireworkTo != null) {
                    fireworkTo.detonate();
                }
                t = t + 0.35;
                double x = Math.sin(t) + Math.sin(t) * radius;
                double y = t-t+1;
                double z = Math.cos(t) + Math.cos(t) * radius;
                if (times == 20 || times == 40 || times == 60) {
                    for (Entity entity : centerLocation.getWorld().getNearbyEntities(centerLocation, radius, radius, radius)) {
                        if (entity instanceof LivingEntity && !entity.equals(caster)) {
                            damageEntity((LivingEntity) entity, caster);
                        }
                    }
                }
                centerLocation.add(x, y, z);
                Firework firework = (Firework) centerLocation.getWorld().spawnEntity(centerLocation, EntityType.FIREWORK);
                firework.setFireworkMeta(getFireworkMeta(firework));
                fireworkTo = firework;
                centerLocation.subtract(x, y, z);

                times++;
                radius -= 0.2;
                if (times > 60) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 1);

        broadcastMessage("&7[&aCarbyne&7]: &5" + caster.getName() + " &ahas casted the &c" + getSpecialName().replace("_", " ") + " &aspecial!", caster.getLocation(), 50);
    }

    public void damageEntity(LivingEntity entity, Player caster) {
        //EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(caster, entity, DamageCause.ENTITY_ATTACK, damagePerRound);
        //Bukkit.getServer().getPluginManager().callEvent(damageEvent);
        entity.damage(damagePerRound);
        //entity.setFireTicks(20 * 5);
    }

    public FireworkMeta getFireworkMeta(Firework firework){
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(false).trail(false).withColor(Color.BLACK).withFade(Color.GRAY).with(FireworkEffect.Type.BURST).build();
        FireworkEffect effect1 = FireworkEffect.builder().trail(false).flicker(false).withColor(Color.GRAY).withFade(Color.GRAY).with(FireworkEffect.Type.BURST).build();
        fireworkMeta.clearEffects();
        fireworkMeta.addEffect(effect);
        fireworkMeta.addEffect(effect1);
        fireworkMeta.setPower(0);
        return fireworkMeta;
    }
}
