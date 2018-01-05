package com.medievallords.carbyne.gear.effects.carbyne.targeters;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface CarbyneActivationTarget {

    void cast(Player caster, Location target);

    void cast(Player caster, LivingEntity target);

    void cast(Player caster);

    boolean canActivate(Player caster);
}
