package com.medievallords.carbyne.mechanics;

import com.medievallords.carbyne.Carbyne;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

/**
 * Created by WE on 2017-08-26.
 */
public class CodingMechanic extends SkillMechanic implements ITargetedEntitySkill {

    public CodingMechanic(String skill, MythicLineConfig mlc, int interval) {
        super(skill, mlc, interval);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        callMechanic(abstractEntity.getBukkitEntity());
        return false;
    }

    private void callMechanic(Entity entity) {
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        new BukkitRunnable() {
            double times = 0;

            @Override
            public void run() {
                times += 0.25;

                int length = 10 + (int) (Math.cos(times) * 7);

                String text = "";
                for (int i = 0; i < length; i++) {
                    text = text + "a";
                }

                player.sendTitle(new Title.Builder().title(ChatColor.MAGIC + text).stay(4).build());

                if (times > 8) {
                    cancel();
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 3);
    }
}
