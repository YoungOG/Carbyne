package com.medievallords.carbyne.missions.object.implementations.missionabstractions;

import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.BlockPlacingMission;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.material.MaterialData;

import java.util.List;

/**
 * Created by Dalton on 8/16/2017.
 */
public class BlockPlacingClass extends Mission implements BlockPlacingMission {

    @Getter
    @Setter
    private MaterialData[] goalData;

    public BlockPlacingClass(String name, String[] description, String timeLimit, int objectiveGoal, double reward, List<String> lootData, MaterialData[] goalData) {
        super(name, description, timeLimit, objectiveGoal, reward, lootData);
        this.goalData = goalData;
    }

    @Override
    public MaterialData[] getGoalBlocks() {
        return goalData;
    }
}
