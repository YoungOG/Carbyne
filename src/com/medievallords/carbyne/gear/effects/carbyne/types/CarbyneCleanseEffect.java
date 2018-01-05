package com.medievallords.carbyne.gear.effects.carbyne.types;

import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffect;
import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffectLine;
import com.medievallords.carbyne.gear.effects.carbyne.targeters.CarbyneRangedTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CarbyneCleanseEffect extends CarbyneEffect implements CarbyneRangedTarget {

    public CarbyneCleanseEffect(CarbyneEffectLine cel) {
        super("Cleanse", cel);
    }

    @Override
    public void cast(Player shooter, LivingEntity target) {

    }

    @Override
    public boolean canActivate(Player caster) {
        return false;
    }
}
