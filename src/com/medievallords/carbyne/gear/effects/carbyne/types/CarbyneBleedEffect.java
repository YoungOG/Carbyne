package com.medievallords.carbyne.gear.effects.carbyne.types;

import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffect;
import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffectLine;
import com.medievallords.carbyne.gear.effects.carbyne.targeters.CarbyneMeleeTarget;
import com.medievallords.carbyne.gear.effects.carbyne.targeters.CarbyneRangedTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-08-26.
 */
public class CarbyneBleedEffect extends CarbyneEffect implements CarbyneMeleeTarget, CarbyneRangedTarget {

    private int interval;

    public CarbyneBleedEffect(CarbyneEffectLine cel) {
        super("Bleed", cel);
    }

    @Override
    public void cast(Player caster, LivingEntity target) {

    }

    @Override
    public boolean canActivate(Player caster) {
        return true;
    }
}
