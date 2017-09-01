package com.medievallords.carbyne.missions.object.implementations.noobmissions;

import com.medievallords.carbyne.missions.enums.Difficulty;
import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.BlockPlacingMission;
import org.bukkit.material.MaterialData;

/**
 * Created by Dalton on 8/17/2017.
 */
public class BuildSomethingMission extends Mission implements BlockPlacingMission {


    public BuildSomethingMission() {
        super(Difficulty.BABY, "&5Build Something", new String[]
                {
                        "&bJust build something! Have fun!"
                }, "12h", 100, 200.0, null);
    }

    @Override
    public MaterialData[] getGoalBlocks() {
        return new MaterialData[0];
    }
}
