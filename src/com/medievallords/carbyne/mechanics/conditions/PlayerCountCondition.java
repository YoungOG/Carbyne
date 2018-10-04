package com.medievallords.carbyne.mechanics.conditions;

import com.medievallords.carbyne.utils.PlayerUtility;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.ILocationCondition;

public class PlayerCountCondition extends SkillCondition implements ILocationCondition {

    private int playerCount = 1, distance = 1;

    public PlayerCountCondition(String line, MythicLineConfig mg) {
        super(line);

        playerCount = mg.getInteger("count");
        distance = mg.getInteger("distance");
    }

    @Override
    public boolean check(AbstractLocation abstractLocation) {
        return PlayerUtility.getPlayersInRadius(BukkitAdapter.adapt(abstractLocation), distance).size() < playerCount;
    }
}
