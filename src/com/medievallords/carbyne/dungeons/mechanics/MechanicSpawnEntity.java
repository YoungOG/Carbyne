package com.medievallords.carbyne.dungeons.mechanics;


import com.medievallords.carbyne.dungeons.mechanics.targeters.Target;
import com.medievallords.carbyne.dungeons.mechanics.targeters.instances.ITargetLocation;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

@Getter
public class MechanicSpawnEntity extends Mechanic implements ITargetLocation {

    private HashMap<String, Integer> entities = new HashMap<>();
    private static final MobManager mobManager = MythicMobs.inst().getMobManager();

    public MechanicSpawnEntity(String type, DungeonLineConfig lineConfig) {
        super(type, lineConfig);

        String[] splitItems = lineConfig.getString("entities", "ZOMBIE:3,").split(",");
        for (String key : splitItems) {
            int amount = 1;
            String[] amountSplit = key.split(":");
            String mobName = amountSplit[0];
            if (amountSplit.length == 2) {
                try {
                    amount = Integer.parseInt(amountSplit[1]);
                } catch (NumberFormatException e) {
                }
            }

            entities.put(mobName, amount);
        }
    }

    public MechanicSpawnEntity(String type, Target target, HashMap<String, Integer> entities) {
        super(type, target);

        this.entities = entities;
    }

    @Override
    public boolean cast(Location location) {
        World world = location.getWorld();

        for (String entityName : entities.keySet()) {
            MythicMob mob = mobManager.getMythicMob(entityName);
            if (mob != null) {
                int amount = 0;
                while (amount < entities.get(entityName)) {
                    mobManager.spawnMob(entityName, location);
                    amount++;
                }
            } else {
                try {
                    EntityType entityType = EntityType.valueOf(entityName.toUpperCase());
                    int amount = 0;
                    while (amount <= entities.get(entityName)) {
                        world.spawnEntity(location, entityType);
                        amount++;
                    }
                } catch (IllegalArgumentException e) {

                }

            }
        }

        return true;
    }
}
