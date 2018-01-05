package com.medievallords.carbyne.gear.effects.carbyne.types;

import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffect;
import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffectLine;
import com.medievallords.carbyne.gear.effects.carbyne.targeters.CarbyneMeleeTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CarbyneFatigueEffect extends CarbyneEffect implements CarbyneMeleeTarget {

    public CarbyneFatigueEffect(String name, CarbyneEffectLine cel) {
        super(name, cel);
    }

    @Override
    public void cast(Player caster, LivingEntity target) {

    }

    @Override
    public boolean canActivate(Player caster) {
        return false;
    }
}
