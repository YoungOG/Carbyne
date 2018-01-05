package com.medievallords.carbyne.gear.effects.carbyne.types;

import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffect;
import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffectLine;
import com.medievallords.carbyne.gear.effects.carbyne.targeters.CarbyneActivationTarget;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CarbyneCursedFieldEffect extends CarbyneEffect implements CarbyneActivationTarget {


    public CarbyneCursedFieldEffect(CarbyneEffectLine cel) {
        super("CursedField", cel);
    }

    @Override
    public void cast(Player caster) {

    }

    @Override
    public boolean canActivate(Player caster) {
        return false;
    }

    @Override
    public void cast(Player caster, Location target) {

    }

    @Override
    public void cast(Player caster, LivingEntity target) {

    }
}
