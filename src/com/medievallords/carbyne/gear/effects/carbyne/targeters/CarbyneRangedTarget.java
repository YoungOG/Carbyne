package com.medievallords.carbyne.gear.effects.carbyne.targeters;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface CarbyneRangedTarget {

    void cast(Player shooter, LivingEntity target);

    boolean canActivate(Player caster);
}
