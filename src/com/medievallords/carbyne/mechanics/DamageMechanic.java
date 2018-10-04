package com.medievallords.carbyne.mechanics;

import com.boydti.fawe.util.TaskManager;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.PlayerHealth;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DamageMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private int damage;

    public DamageMechanic(String skill, MythicLineConfig mlc, int interval) {
        super(skill, mlc, interval);

        this.damage = mlc.getInteger("damage", 1);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (abstractEntity.getHealth() < 0 || abstractEntity.isDead()) {
            return false;
        }

        if (abstractEntity.isPlayer()) {
            Player player = (Player) abstractEntity.getBukkitEntity();
            if (player.getGameMode() != GameMode.SURVIVAL || player.getGameMode() != GameMode.ADVENTURE) {
                return false;
            }

            PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    playerHealth.setHealth(playerHealth.getHealth() - damage, player);
                    if (skillMetadata.getCaster() != null) {
                        player.damage(0.0, skillMetadata.getCaster().getEntity().getBukkitEntity());
                    } else {
                        player.damage(0.0);
                    }
                }
            }.runTask(Carbyne.getInstance());
            return true;
        } else if (abstractEntity.isLiving()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (skillMetadata.getCaster() != null) {
                        ((LivingEntity) abstractEntity.getBukkitEntity()).damage(damage, skillMetadata.getCaster().getEntity().getBukkitEntity());
                    } else {
                        ((LivingEntity) abstractEntity.getBukkitEntity()).damage(damage);
                    }
                }
            }.runTask(Carbyne.getInstance());

            return true;
        }

        return false;
    }
}
