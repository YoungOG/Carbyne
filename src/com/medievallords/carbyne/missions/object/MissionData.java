package com.medievallords.carbyne.missions.object;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.missions.enums.Difficulty;
import com.medievallords.carbyne.missions.object.implementations.missionabstractions.*;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Dalton on 8/16/2017.
 */
@Getter
@Setter
public class MissionData {

    public static List<Class> materialDataClass = new ArrayList<Class>() {{
        add(BlockBreakingClass.class);
        add(BlockPlacingClass.class);
        add(FishingClass.class);
    }};
    public static List<Class> itemStackClass = new ArrayList<Class>() {{
        add(PlayerItemPickupClass.class);
    }};
    public static List<Class> entityDataClass = new ArrayList<Class>() {{
        add(KillEntityClass.class);
    }};
    public static List<Class> bossClasses = new ArrayList<Class>() {{
        add(BossHuntClass.class);
    }};

    private final Class missionType;
    private final String missionName;
    private final String[] missionDescription;
    private final String timeLimit;
    private final int objectiveGoal;
    private final double reward;
    private MaterialData[] goalData;
    private List<String> lootData, solutionData;

    public MissionData(Class missionType, String missionName, String[] missionDescription, String timeLimit, int objectiveGoal, double reward, List<String> lootData) {
        this.missionType = missionType;
        this.missionName = missionName;
        this.missionDescription = missionDescription;
        this.timeLimit = timeLimit;
        this.objectiveGoal = objectiveGoal;
        this.reward = reward;
        this.lootData = lootData;
    }

    public MissionData(Class missionType, String missionName, String[] missionDescription, String timeLimit, int objectiveGoal, double reward, List<String> lootData, MaterialData[] goalData) {
        this.missionType = missionType;
        this.missionName = missionName;
        this.missionDescription = missionDescription;
        this.timeLimit = timeLimit;
        this.objectiveGoal = objectiveGoal;
        this.reward = reward;
        this.goalData = goalData;
        this.lootData = lootData;
    }

    public MissionData(Class missionType, String missionName, String[] missionDescription, String timeLimit, int objectiveGoal, double reward, List<String> lootData, List<String> solutionData) {
        this.missionType = missionType;
        this.missionName = missionName;
        this.missionDescription = missionDescription;
        this.timeLimit = timeLimit;
        this.objectiveGoal = objectiveGoal;
        this.reward = reward;
        this.lootData = lootData;
        this.solutionData = solutionData;
    }

    public static Class getMissionType(String missionType) {
        MessageManager.broadcastMessage(missionType);
        switch (missionType.toLowerCase()) {
            case "blockbreakingmission": {
                return BlockBreakingClass.class;
            }
            case "blockplacingmission": {
                return BlockPlacingClass.class;
            }
            case "fishingmission": {
                return FishingClass.class;
            }
            case "jointownmission": {
                return TownJoinedOrCreatedClass.class;
            }
            case "playerpickupitemmission": {
                return PlayerItemPickupClass.class;
            }
            case "killplayermission": {
                return KillPlayerClass.class;
            }
            case "bosshuntmission": {
                return BossHuntClass.class;
            }
            case "killentitymission": {
                return KillEntityClass.class;
            }
            default: {
                return null;
            }
        }
    }

    public Mission instantiate() {
        Mission mission = null;
        if (materialDataClass.contains(missionType)) {
            mission = Mission.instanitate(missionType, new Object[]{missionName, missionDescription, timeLimit, objectiveGoal, reward, lootData, goalData});
        } else if (itemStackClass.contains(missionType)) {
            mission = Mission.instanitate(missionType, new Object[]{missionName, missionDescription, timeLimit, objectiveGoal, reward, lootData, solutionData});
        } else if (entityDataClass.contains(missionType)) {
            mission = Mission.instanitate(missionType, new Object[]{missionName, missionDescription, timeLimit, objectiveGoal, reward, lootData, solutionData});
        } else if (bossClasses.contains(missionType)) {
            mission = Mission.instanitate(missionType, new Object[]{missionName, missionDescription, timeLimit, objectiveGoal, reward, lootData, solutionData});
            mission.changeDifficulty(this, Difficulty.BOSS);
        } else {
            mission = Mission.instanitate(missionType, new Object[]{missionName, missionDescription, timeLimit, objectiveGoal, reward, lootData});
        }

        if (mission == null) {
            Carbyne.getInstance().getLogger().log(Level.SEVERE, "Failed to instan mission with fields " + missionName + " " + missionDescription + " " + timeLimit +
                    " " + objectiveGoal + " " + reward + " " + lootData);
        }

        return mission;
    }

}
