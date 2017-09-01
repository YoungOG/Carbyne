package com.medievallords.carbyne.missions.object.implementations.missionabstractions;

import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.KillEntityMission;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dalton on 8/23/2017.
 */
public class KillEntityClass extends Mission implements KillEntityMission {

    private List<EntityType> entityTypes;

    public KillEntityClass(String name, String[] description, String timeLimit, int objectiveGoal, double reward, List<String> lootData, List<String> entityNames) {
        super(name, description, timeLimit, objectiveGoal, reward, lootData);
        entityTypes = new ArrayList<>();
        for (int i = 0; i < entityNames.size(); i++) {
            String entityName = entityNames.get(i).toUpperCase();
            EntityType type;
            if ((type = EntityType.valueOf(entityName)) != null) entityTypes.add(type);
        }
    }

    @Override
    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }
}
