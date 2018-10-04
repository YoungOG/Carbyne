package com.medievallords.carbyne.dungeons.dungeons;

import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class MobData {

    private HashMap<MythicMob, Integer> mobs;
    private int maxAmount;

    public MobData(HashMap<MythicMob, Integer> mobs, int maxAmount) {
        this.mobs = mobs;
        this.maxAmount = maxAmount;
    }

    public void setAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }
}

