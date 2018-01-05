package com.medievallords.carbyne.gear.effects.carbyne.types;

import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffect;
import com.medievallords.carbyne.gear.effects.carbyne.CarbyneEffectLine;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.targeted.SilenceSpell;

public class CarbyneSilenceEffect extends CarbyneEffect {

    private double chance, radius;
    private float power;
    private SilenceSpell silence = (SilenceSpell) MagicSpells.getSpellByInternalName("Silence");

    public CarbyneSilenceEffect(CarbyneEffectLine cel) {
        super("Silence", cel);

        this.chance = cel.getDouble("chance", 1);
        this.radius = cel.getDouble("radius", 1);
        this.power = cel.getInt("power", 1);
    }

    /*@Override
    public void callEffect(Entity attacked, Entity attacker) {
        if (!(attacked instanceof Player)) {
            return;
        }

        Player caster = (Player) attacked;

        if (Math.random() > chance) {
            return;
        }

        List<Entity> entities = attacked.getNearbyEntities(radius, radius, radius);


        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (!(entities instanceof Player)) {
                return;
            }

            Player player = (Player) entity;
            silence.castAtEntity(caster, player, power);
        }
    }*/
}
