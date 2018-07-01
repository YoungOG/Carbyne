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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-03-19.
 * for the Carbyne project.
 */
public class ShootingStar implements Special, Listener {

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
        run(loc.clone().add(0, 18, 0), caster);

        broadcastMessage("&7[&aCarbyne&7]: &5" + caster.getName() + " &ahas casted the &c" + getSpecialName().replace("_", " ") + " &aspecial!", caster.getLocation(), 50);
    }

    private void run(final Location centerLocation, final Player caster) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : centerLocation.getWorld().getNearbyEntities(centerLocation.clone().subtract(0, 18, 0), 10, 10, 10)) {
                    if (entity.getType() == EntityType.PLAYER) {
                        Player player = (Player) entity;
                        if (!player.equals(caster)) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1));
                        }
                    }
                }
            }
        }.runTaskLater(Carbyne.getInstance(), 60);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (double t = 0; t <= (Math.PI); t += Math.PI / 10) {
                    final double x = Math.sin(t) * 7.0;
                    final double z = Math.cos(t) * 7.0;

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            centerLocation.add(x, 0, z);
                            InstantFirework.spawn(centerLocation, effects);
                            entities.add(centerLocation.getWorld().spawnFallingBlock(centerLocation, Material.SNOW_BLOCK, (byte) 0));
                            centerLocation.subtract(x, 0, z);
                            centerLocation.subtract(x, 0, z);
                            InstantFirework.spawn(centerLocation, effects);
                            entities.add(centerLocation.getWorld().spawnFallingBlock(centerLocation, Material.CLAY, (byte) 0));
                            centerLocation.add(x, 0, z);
                        }
                    }.runTask(Carbyne.getInstance());
                }
            }
        }.runTaskAsynchronously(Carbyne.getInstance());
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
        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc, 50, false);
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
