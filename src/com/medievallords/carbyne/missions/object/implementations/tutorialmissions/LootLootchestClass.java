package com.medievallords.carbyne.missions.object.implementations.tutorialmissions;

import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.LootLootchestMission;

import java.util.List;

/**
 * Created by Williams on 2017-09-06
 * for the Carbyne project.
 */
public class LootLootchestClass extends Mission implements LootLootchestMission {

    private int amount = 1;

    public LootLootchestClass(String name, String[] description, String timeLimit, int objectiveGoal, double reward, List<String> lootData, int amount) {
        super(name, description, timeLimit, objectiveGoal, reward, lootData);
        this.amount = amount;
    }

    @Override
    public int getAmount() {
        return amount;
    }
}
