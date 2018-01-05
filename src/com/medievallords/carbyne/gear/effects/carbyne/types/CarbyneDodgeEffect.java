package com.medievallords.carbyne.gear.effects.carbyne.types;

import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffect;
import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffectLine;
import com.medievallords.carbyne.gear.effects.carbyne.targeters.CarbynePassiveTarget;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CarbyneDodgeEffect extends CarbyneEffect implements CarbynePassiveTarget {


    public CarbyneDodgeEffect(CarbyneEffectLine cel) {
        super("Dodge", cel);
    }

    @Override
    public void cast(Player caster, Location target) {

    }

    @Override
    public void cast(Player caster, LivingEntity target) {

    }

    @Override
    public void cast(Player caster) {

    }

    @Override
    public boolean canActivate(Player caster) {
        return false;
    }
}
