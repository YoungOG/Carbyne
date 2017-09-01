package com.medievallords.carbyne.missions.object.implementations.missionabstractions;

import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.interfaces.KillPlayerMission;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dalton on 8/21/2017.
 */
public class KillPlayerClass extends Mission implements KillPlayerMission {

    List<Player> killedPlayers;

    public KillPlayerClass(String name, String[] description, String timeLimit, int objectiveGoal, double reward, List<String> lootData) {
        super(name, description, timeLimit, objectiveGoal, reward, lootData);
        killedPlayers = new ArrayList<>();
    }

    @Override
    public List<Player> getKilledPlayers() {
        return killedPlayers;
    }
}
