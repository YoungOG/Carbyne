package com.medievallords.carbyne.missions.object.implementations.missionabstractions;

import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.BossHuntMission;

import java.util.List;

/**
 * Created by Dalton on 8/21/2017.
 */
public class BossHuntClass extends Mission implements BossHuntMission {

    List<String> bossNames;

    public BossHuntClass(String name, String[] description, String timeLimit, int objectiveGoal, double reward, List<String> lootData, List<String> bossNames) {
        super(name, description, timeLimit, objectiveGoal, reward, lootData);
        this.bossNames = bossNames;
    }

    @Override
    public List<String> getBossNames() {
        return bossNames;
    }
}
