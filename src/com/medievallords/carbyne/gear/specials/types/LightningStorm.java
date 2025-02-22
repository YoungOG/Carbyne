package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.utils.PlayerHealth;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by xwiena22 on 2017-03-13.
 */
public class LightningStorm implements Special {

    private final double radius = 5;
    private final int maxTimes = 3;
    private final double damagePerLightning = 13;

    @Override
    public String getSpecialName() {
        return "Lightning_Storm";
    }

    @Override
    public int getRequiredCharge() {
        return 50;
    }

    @Override
    public void callSpecial(final Player caster) {
        final Location center = caster.getTargetBlock((Set<Material>) null, 9).getLocation();
        final List<LivingEntity> entitiesToHit = new ArrayList<>();
        int times = 0;
        for (final Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
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
            for (final LivingEntity entity : entitiesToHit) {
                entity.getWorld().strikeLightningEffect(entity.getLocation());
                damageEntity(entity);
                entity.getWorld().playEffect(entity.getEyeLocation(), Effect.VOID_FOG, 3);
            }
        }

        broadcastMessage("&7[&aCarbyne&7]: &5" + caster.getName() + " &ahas casted the &c" + getSpecialName().replace("_", " ") + " &aspecial!", caster.getLocation(), 50);
    }

    private void damageEntity(LivingEntity entity) {
        if (!isInSafeZone(entity)) {
            if (entity instanceof Player) {
                PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(entity.getUniqueId());
                playerHealth.setHealth(playerHealth.getHealth() * 0.5, (Player) entity);
                entity.damage(0.0);
                entity.setFireTicks(20 * 5);
                return;
            }

            entity.damage(damagePerLightning);
            entity.setFireTicks(20 * 5);
        }
    }
}
