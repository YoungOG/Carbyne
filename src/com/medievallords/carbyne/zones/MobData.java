package com.medievallords.carbyne.zones;

import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Getter;

@Getter
public class MobData {

    private MythicMob mob;
    private int chance;

    public MobData(MythicMob mob, int chance) {
        this.mob = mob;
        this.chance = chance;
    }
}
