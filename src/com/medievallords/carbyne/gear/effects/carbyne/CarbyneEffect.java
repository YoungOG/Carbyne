package com.medievallords.carbyne.gear.effects.carbyne;

/**
 * Created by WE on 2017-08-26.
 */
public abstract class CarbyneEffect {

    private String name;

    public CarbyneEffect(String name, CarbyneEffectLine cel) {
        this.name = name;
    }

    //public abstract void callEffect(Entity attacked, Entity attacker);
}
