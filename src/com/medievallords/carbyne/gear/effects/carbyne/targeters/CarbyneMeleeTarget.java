package com.medievallords.carbyne.gear.effects.carbyne.targeters;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface CarbyneMeleeTarget {

    void cast(Player caster, LivingEntity target);

    boolean canActivate(Player caster);
}
