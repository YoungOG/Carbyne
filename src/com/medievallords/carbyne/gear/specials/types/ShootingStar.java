package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.utils.InstantFirework;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-03-19.
 * for the Carbyne project.
 */
public class ShootingStar implements Special, Listener {

    private final int interval = 9;
    private final List<Entity> entities = new ArrayList<>();
    private final FireworkEffect[] effects;

    public ShootingStar() {
        this.effects = getFireworkEffect();
        Bukkit.getPluginManager().registerEvents(this, Carbyne.getInstance());
    }

    @Override
    public int getRequiredCharge() {
        return 50;
    }

    @Override
    public String getSpecialName() {
        return "Shooting_Star";
    }

    @Override
    public void callSpecial(Player caster) {
        final Location loc = caster.getLocation();
        InstantFirework.spawn(loc, effects);
        run(loc.clone().add(0, 18, 0));

        broadcastMessage("&7[&aCarbyne&7]: &5" + caster.getName() + " &ahas casted the &c" + getSpecialName().replace("_", " ") + " &aspecial!", caster.getLocation(), 50);
    }

    private void run(final Location centerLocation) {
        new BukkitRunnable() {
            private double t = 0;
            private double times = 0;
            private double radius = 2;
            private int tir = 0;

            @Override
            public void run() {
                t += 0.25;
                final double x = Math.sin(t) + Math.sin(t) * radius;
                final double z = Math.cos(t) + Math.cos(t) * radius;
                final List<Player> players = new ArrayList<>();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (tir >= interval) {
                            centerLocation.add(x, 0, z);
                            InstantFirework.spawn(centerLocation, players, effects);
                            entities.add(centerLocation.getWorld().spawnFallingBlock(centerLocation, Material.SNOW_BLOCK, (byte) 0));
                            centerLocation.subtract(x, 0, z);
                            centerLocation.subtract(x, 0, z);
                            InstantFirework.spawn(centerLocation, players, effects);
                            entities.add(centerLocation.getWorld().spawnFallingBlock(centerLocation, Material.CLAY, (byte) 0));
                            centerLocation.add(x, 0, z);
                            tir = 0;
                        } else {
                            centerLocation.add(x, 0, z);
                            InstantFirework.spawn(centerLocation, players, effects);
                            centerLocation.subtract(x, 0, z);
                            centerLocation.subtract(x, 0, z);
                            InstantFirework.spawn(centerLocation, players, effects);
                            centerLocation.add(x, 0, z);
                        }
                    }
                }.runTask(Carbyne.getInstance());

                tir++;
                radius += .1;
                times++;
                if (times > 60) {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 27, 1);
    }

    @EventHandler
    public void onLand(final EntityChangeBlockEvent event) {
        if (entities.contains(event.getEntity())) {
            event.setCancelled(true);
            entities.remove(event.getEntity());
            makeExplosion(event.getEntity().getLocation());
        }
    }

    private void makeExplosion(final Location loc) {
        ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 1, loc, 50, false);
        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 3, 3, 3)) {
            if (entity.getType() == EntityType.PLAYER) {
                ((Player) entity).damage(4);
            }
        }

        loc.getWorld().playSound(loc, Sound.EXPLODE, 2, 1.3f);
    }

    public FireworkEffect[] getFireworkEffect() {
        FireworkEffect effect = FireworkEffect.builder().flicker(false).trail(false).withColor(Color.WHITE).withFade(Color.TEAL).with(FireworkEffect.Type.BURST).build();
        FireworkEffect effect1 = FireworkEffect.builder().trail(false).flicker(false).withColor(Color.AQUA).withFade(Color.GRAY).with(FireworkEffect.Type.BURST).build();
        return new FireworkEffect[]{
                effect, effect1
        };
    }

}
