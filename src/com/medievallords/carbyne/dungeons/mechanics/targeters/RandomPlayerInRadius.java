package com.medievallords.carbyne.dungeons.mechanics.targeters;

import com.medievallords.carbyne.utils.Maths;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.targeters.IEntitySelector;

import java.util.HashSet;

public class RandomPlayerInRadius extends IEntitySelector {

    private int radius;

    public RandomPlayerInRadius(MythicLineConfig mlc) {
        super(mlc);

        this.radius = mlc.getInteger("radius", 10);
    }

    @Override
    public HashSet<AbstractEntity> getEntities(SkillMetadata skillMetadata) {
        HashSet<AbstractEntity> entities = new HashSet<>();
        int r = Maths.randomNumberBetween(skillMetadata.getEntityTargets().size(), 0);
        entities.add(skillMetadata.getCaster().getEntity().getWorld().getPlayersNearLocation(skillMetadata.getCaster().getLocation(), radius).get(r));
        return entities;
    }
}
