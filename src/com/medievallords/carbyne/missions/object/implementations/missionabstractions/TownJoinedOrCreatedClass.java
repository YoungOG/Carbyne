package com.medievallords.carbyne.missions.object.implementations.missionabstractions;

import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.JoinTownMission;

import java.util.List;

/**
 * Created by Dalton on 8/16/2017.
 */
public class TownJoinedOrCreatedClass extends Mission implements JoinTownMission {

    public TownJoinedOrCreatedClass(String name, String[] description, String timeLimit, int objectiveGoal, List<String> lootData, double reward) {
        super(name, description, timeLimit, objectiveGoal, reward, lootData);
    }
}
