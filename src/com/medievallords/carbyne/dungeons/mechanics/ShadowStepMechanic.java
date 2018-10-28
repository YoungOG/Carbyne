package com.medievallords.carbyne.dungeons.mechanics;

import com.nisovin.magicspells.util.BlockUtils;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class ShadowStepMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {

    public ShadowStepMechanic(String skill, MythicLineConfig mlc, int interval) {
        super(skill, mlc, interval);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        final AbstractLocation loc = abstractEntity.getLocation();
        final Block b = BukkitAdapter.adapt(loc).getBlock();
        if (!BlockUtils.isPathable(b.getType()) || !BlockUtils.isPathable(b.getRelative(BlockFace.UP))) {
            return false;
        }

        skillMetadata.getCaster().getEntity().teleport(loc);
        return true;
    }

    @Override
    public boolean castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation) {
        final Block b = BukkitAdapter.adapt(abstractLocation).getBlock();
        if (!BlockUtils.isPathable(b.getType()) || !BlockUtils.isPathable(b.getRelative(BlockFace.UP))) {
            return false;
        }

        skillMetadata.getCaster().getEntity().teleport(abstractLocation);
        return true;
    }
}
