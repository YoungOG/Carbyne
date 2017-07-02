package com.medievallords.carbyne.donator;

import org.bukkit.entity.Player;

/**
 * Created by Dalton on 6/28/2017.
 */
public abstract class AdvancedEffect
{

    protected Player player;

    public AdvancedEffect(Player player)
    {
        this.player = player;
    }

    public abstract void tick();

    public static final String emeraldTwirlPemission = "carbyne.effects.advanced.emeraldtwirl";
    public static final String loveShieldPermission = "carbyne.effects.advanced.loveshield";
    public static final String scannerPermission = "carbyne.effects.advanced.scanner";
    public static final String waterSpiralPermission = "carbyne.effects.advanced.waterspiral";
    public static final String yinYangPermission = "carbyne.effects.advanced.yinyang";
    public static final String boxPermission = "carbyne.effects.advanced.box";

    public static final String trailOrigamiStar = "carbyne.effects.trail.origamistar";
    public static final String trailWarning = "carbyne.effects.trail.warning";
    public static final String trailFlame = "carbyne.effects.trail.flame";
    public static final String trailSpell = "carbyne.effects.trail.spell";
    public static final String trailCloud = "carbyne.effects.trail.cloud";
    public static final String trailCrit = "carbyne.effects.trail.crit";
    public static final String trailMagicCrit = "carbyne.effects.trail.magiccrit";
    public static final String trailWater = "carbyne.effects.trail.water";
    public static final String trailWaterDrop = "carbyne.effects.trail.waterdrop";
    public static final String trailSnow = "carbyne.effects.trail.snow";
    public static final String trailLavaDrip = "carbyne.effects.trail.lavadrip";
    public static final String trailMagicLetters = "carbyne.effects.trail.magicletters";
    public static final String trailExplosion = "carbyne.effects.trail.explosion";
    public static final String trailSpark = "carbyne.effects.trail.spark";
    public static final String trailHearts = "carbyne.effects.trail.hearts";
    public static final String trailMagicDust = "carbyne.effects.trail.magicdust";
    public static final String trailMusic = "carbyne.effects.trail.music";
    public static final String trailWitchSpell = "carbyne.effects.trail.witchspell";

}
