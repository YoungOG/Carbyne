package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by xwiena22 on 2017-03-13.
 *
 */
public class BastionOfHealth implements Special {


    @Override
    public String getSpecialName() {
        return "Bastion_Of_Health";
    }

    @Override
    public int getRequiredCharge() {
        return 50;
    }

    @Override
    public void callSpecial(Player caster) {

        new BukkitRunnable() {

            double radius = 4;
            double t = 0;
            double times = 0;

            public void run() {
                Location loc = caster.getLocation();
                double x = Math.sin(t) * radius;
                double y = 0.2;
                double z = Math.cos(t) * radius;
                double x2 = Math.cos(t) * radius;
                double y2 = 0.2;
                double z2 = Math.sin(t) * radius;
                loc.add(x, y, z);
                ParticleEffect.HEART.display(0f, 0f, 0f, 0f, 1, loc, 20, false);
                loc.subtract(x, y, z);
                loc.add(x2, y2, z2);
                ParticleEffect.HEART.display(0f, 0f, 0f, 0f, 1, loc, 20, false);
                loc.subtract(x2, y2, z2);
                times++;
                t += Math.PI / 32;
                if (times > 100) {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 1);
        new BukkitRunnable() {

            double times = 0;

            public void run() {
                healPlayer(caster);

                times += 0.5;
                if (times > 5) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 10);

        broadcastMessage("&7[&aCarbyne&7]: &5" + caster.getName() + " &ahas casted the &c" + getSpecialName().replace("_", " ") + " &aspecial!", caster.getLocation(), 50);
    }

    public void healPlayer(Player player) {
        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 4, 4, 4)) {
            if (entity instanceof Player) {
                Player to = (Player) entity;
                if (isOnSameTeam(player, to) || player.getUniqueId().equals(to.getUniqueId())) {
                    to.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                }
            }
        }
    }
}
