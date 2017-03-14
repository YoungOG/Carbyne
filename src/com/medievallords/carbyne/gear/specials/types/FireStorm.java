package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Williams on 2017-03-12.
 */
public class FireStorm implements Special{

    private double damagePerRound = 5;
    private Firework fireworkTo;

    @Override
    public int getRequiredCharge() {
        return 0;
    }

    @Override
    public String getSpecialName() {
        return "FireStorm";
    }

    @Override
    public void callSpecial(Player caster, Location centerLocation, CarbyneWeapon carbyneWeapon) {
        new BukkitRunnable() {

            double t = 0;
            double times = 0;
            double radius = 0.2;
            Location copy = centerLocation.clone();
            @Override
            public void run() {
                t = t + 0.35;
                if (times == Math.floor(times)) {
                    for (Entity entity : copy.getWorld().getNearbyEntities(copy, radius, radius, radius)) {
                        if (entity instanceof LivingEntity && !entity.equals(caster)) {
                            damageEntity((LivingEntity) entity, caster);
                        }
                    }
                }
                for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
                    double y = Math.cos(i);
                    double r = Math.sin(i) + radius;
                    for (double a = 0; a < Math.PI * 2; a+= Math.PI / 10) {
                        double x = Math.cos(a) * r + radius;
                        double z = Math.sin(a) * r + radius;
                        copy.add(x, y, z);

                        ParticleEffect.FLAME.display(0.0F, 0.0F, 0.0F, 0.0F, 1, copy, 30);

                        copy.subtract(x, y, z);
                    }
                }
                for (int to = 0; to < 5; to++) {
                    double xF = Math.sin(Math.random() * 10) * radius;
                    double yF = 0;
                    double zF = Math.cos(Math.random() * 10) * radius;
                    copy.add(xF, yF, zF);
                    if (fireworkTo != null) {
                        fireworkTo.detonate();
                    }
                    Firework firework = copy.getWorld().spawn(copy, Firework.class);
                    firework.setFireworkMeta(getFireworkMeta(firework));
                    fireworkTo = firework;
                    copy.subtract(xF, yF, zF);
                }

                times += 0.05;
                radius += 0.2;
                if (times >= 3) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 1);
        carbyneWeapon.setCharge(0);
    }

    public void damageEntity(LivingEntity entity, Player caster) {
        //EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(caster, entity, DamageCause.ENTITY_ATTACK, damagePerRound);
        //Bukkit.getServer().getPluginManager().callEvent(damageEvent);
        entity.damage(damagePerRound);
        //entity.setFireTicks(20 * 5);
    }

    public FireworkMeta getFireworkMeta(Firework firework){
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(false).trail(false).withColor(Color.ORANGE).withFade(Color.YELLOW).with(Type.BURST).build();
        FireworkEffect effect1 = FireworkEffect.builder().trail(false).flicker(false).withColor(Color.YELLOW).withFade(Color.YELLOW).with(Type.BURST).build();
        fireworkMeta.clearEffects();
        fireworkMeta.addEffect(effect);
        fireworkMeta.addEffect(effect1);
        fireworkMeta.setPower(0);
        return fireworkMeta;
    }
}
