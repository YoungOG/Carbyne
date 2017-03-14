package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xwiena22 on 2017-03-13.
 */
public class LightningStorm implements Special{

    private double radius = 5;
    private int maxTimes = 3;
    private double damagePerLightning = 13;

    @Override
    public String getSpecialName() {
        return "LightningStorm";
    }

    @Override
    public int getRequiredCharge() {
        return 0;
    }

    @Override
    public void callSpecial(Player caster, Location centerLocation, CarbyneWeapon carbyneWeapon) {
        List<LivingEntity> entitiesToHit = new ArrayList<>();
        int times = 0;
        for (Entity entity : centerLocation.getWorld().getNearbyEntities(centerLocation, radius, radius, radius)) {
            if (times >= maxTimes) {
                return;
            }
            else if (entity instanceof LivingEntity && !entity.equals(caster)) {
                if (entity instanceof Player) {
                    Player toHit = (Player) entity;
                    if (!isOnSameTeam(caster, toHit)) {
                        entitiesToHit.add(toHit);
                        times++;
                    }
                }
                else {
                    entitiesToHit.add((LivingEntity) entity);
                }
            }
        }
        if (!entitiesToHit.isEmpty()) {
            for (LivingEntity entity : entitiesToHit) {
                entity.getWorld().strikeLightningEffect(entity.getLocation());
                damageEntity(entity, caster);
                entity.getWorld().playEffect(entity.getEyeLocation(), Effect.VOID_FOG, 3);
            }
            carbyneWeapon.setCharge(0);
        }
    }

    public void damageEntity(LivingEntity entity, Player caster) {
        //EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(caster, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damagePerLightning);
        //Bukkit.getServer().getPluginManager().callEvent(damageEvent);
        entity.damage(damagePerLightning);
        //entity.setFireTicks(20 * 5);
    }
}
