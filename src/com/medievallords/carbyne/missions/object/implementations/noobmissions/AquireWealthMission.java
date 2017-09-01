package com.medievallords.carbyne.missions.object.implementations.noobmissions;

import com.medievallords.carbyne.missions.enums.Difficulty;
import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.BlockBreakingMission;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

/**
 * Created by Dalton on 8/17/2017.
 */
public class AquireWealthMission extends Mission implements BlockBreakingMission {


    public AquireWealthMission() {
        super(Difficulty.BABY, "&eAquire Wealth", new String[]
                {
                        "&bThe economy on this server is gold nugget based!",
                        "&bMine some gold and craft it into nuggets. ",
                        "Then use /deposit all to add them to your account."
                }, "12h", 10, 100.0, null);
    }

    @Override
    public MaterialData[] getGoalMaterials() {
        return new MaterialData[]{new MaterialData(Material.GOLD_ORE, (byte) 0)};
    }
}
