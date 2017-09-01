package com.medievallords.carbyne.missions.object.implementations.missionabstractions;

import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.BlockBreakingMission;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.material.MaterialData;

import java.util.List;

/**
 * Created by Dalton on 8/16/2017.
 */
public class BlockBreakingClass extends Mission implements BlockBreakingMission {

    @Getter
    @Setter
    private MaterialData[] goalData;

    public BlockBreakingClass(String name, String[] description, String timeLimit, int objectiveGoal, double reward, List<String> lootData, MaterialData[] goal) {
        super(name, description, timeLimit, objectiveGoal, reward, lootData);
        this.goalData = goal;
    }

    @Override
    public MaterialData[] getGoalMaterials() {
        return goalData;
    }
}
