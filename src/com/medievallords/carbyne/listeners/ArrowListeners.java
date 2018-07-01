package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class ArrowListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Projectile projectile = (Projectile) event.getDamager();

            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) event.getEntity();

                double landY = projectile.getLocation().getY(),
                        damagedPlayerY = entity.getLocation().getY(),
                        landingLocY = landY - damagedPlayerY;

                if (landingLocY > 1.5) {
                    if (Cooldowns.getCooldown(entity.getUniqueId(), "HeadshotCooldown") > 0)
                        return;

                    event.setDamage(event.getDamage() * 4);
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 8 * 20, 4, false, false));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 8 * 30, 1, false, false));

                    for (int i = 0; i < 10; ++i)
                        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.5F, 0.1F, 0.5F, 1.0F, 25, entity.getLocation(), 50, false);

                    if (projectile.getShooter() instanceof Player) {
                        Player shooter = (Player) projectile.getShooter();
                        shooter.playSound(shooter.getLocation(), Sound.ORB_PICKUP, 10.0f, ThreadLocalRandom.current().nextFloat());
                        MessageManager.sendMessage(shooter, "&aYou have shot &5" + (entity.getCustomName() != null ? entity.getCustomName() : entity.getName()) + " &ain the head and dealt bonus damage!");
                    }

                    Cooldowns.setCooldown(entity.getUniqueId(), "HeadshotCooldown", 15000);

                    return;
                }

                if (landingLocY > 0.1 && landingLocY < 0.75) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6 * 20, 0, false, false));

                    for (int i = 0; i < 10; ++i)
                        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.5F, 0.1F, 0.5F, 1.0F, 25, entity.getLocation(), 50, false);

                    if (projectile.getShooter() instanceof Player) {
                        Player shooter = (Player) projectile.getShooter();
                        shooter.playSound(shooter.getLocation(), Sound.ORB_PICKUP, 10.0f, ThreadLocalRandom.current().nextFloat());
                        MessageManager.sendMessage(shooter, "&aYou have shot &5" + (entity.getCustomName() != null ? entity.getCustomName() : entity.getName()) + " &ain the knee and crippled them!");
                    }

                    return;
                }
                if (landingLocY > 0.1 && landingLocY < 1.0) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30 * 20, 1, false, false));

                    for (int i = 0; i < 10; ++i)
                        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.5F, 0.1F, 0.5F, 1.0F, 25, entity.getLocation(), 50, false);

                    if (projectile.getShooter() instanceof Player) {
                        Player shooter = (Player) projectile.getShooter();
                        shooter.playSound(shooter.getLocation(), Sound.ORB_PICKUP, 10.0f, ThreadLocalRandom.current().nextFloat());
                        MessageManager.sendMessage(shooter, "&aYou have shot &5" + (entity.getCustomName() != null ? entity.getCustomName() : entity.getName()) + " &ain the leg and crippled them!");
                    }
                }
            }
        }

        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();

                if (arrow.getShooter() != null)
                    if (arrow.getShooter() instanceof Player) {
                        Player shooter = (Player) arrow.getShooter();

                        shooter.damage(((Player) event.getEntity()).getHealth() - event.getDamage(), event.getEntity());
                        event.setDamage(0.0);
                    } else if (arrow.getShooter() instanceof LivingEntity) {
                        ((LivingEntity) arrow.getShooter()).damage(((Player) event.getEntity()).getHealth() - event.getDamage(), event.getEntity());
                        event.setDamage(0.0);
                    }
            }
        }
    }
}
