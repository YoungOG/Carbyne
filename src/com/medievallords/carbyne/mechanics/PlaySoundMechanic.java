package com.medievallords.carbyne.mechanics;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.entity.Player;

public class PlaySoundMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private String soundName;
    private float pitch, volume;

    public PlaySoundMechanic(String skill, MythicLineConfig mlc, int interval) {
        super(skill, mlc, interval);

        this.soundName = mlc.getString("sound", "note.bass");
        this.pitch = mlc.getFloat("pitch", 1);
        this.volume = mlc.getFloat("volume", 1);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (abstractEntity.isPlayer()) {
            Player player = BukkitAdapter.adapt(abstractEntity.asPlayer());
            player.playSound(BukkitAdapter.adapt(abstractEntity.getLocation()), soundName, volume, pitch);
            return true;
        } else {
            return false;
        }
    }
}
