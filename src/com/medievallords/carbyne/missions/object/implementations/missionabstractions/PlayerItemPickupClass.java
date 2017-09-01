package com.medievallords.carbyne.missions.object.implementations.missionabstractions;

import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.PlayerItemPickupMission;

import java.util.List;

/**
 * Created by Dalton on 8/17/2017.
 */
public class PlayerItemPickupClass extends Mission implements PlayerItemPickupMission {

    List<String> goalMaterials;

    public PlayerItemPickupClass(String name, String[] description, String timeLimit, int objectiveGoal, double reward, List<String> lootData, List<String> solutionTable) {
        super(name, description, timeLimit, objectiveGoal, reward, lootData);
        this.goalMaterials = solutionTable;
    }

    @Override
    public List<String> getItemsForPickup() {
        return goalMaterials;
    }
}
