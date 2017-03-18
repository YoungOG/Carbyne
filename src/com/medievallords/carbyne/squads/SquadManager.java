package com.medievallords.carbyne.squads;

import com.medievallords.carbyne.utils.MessageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Calvin on 3/10/2017
 * for the Carbyne project.
 */
public class SquadManager {

    private List<Squad> squads = new ArrayList<>();

    public void createSquad(UUID leader) {
        if (getSquad(leader) != null) {
            MessageManager.sendMessage(leader, "&cYou are already in a squad.");
            return;
        }

        Squad squad = new Squad(leader);
        squads.add(squad);

        MessageManager.sendMessage(leader, "&aYou have created a new squad.\n&eUse &a/squad &eto view all available squad commands.");
    }

    public Squad getSquad(UUID uniqueId) {
        for (Squad squad : getSquads()) {
            if (squad.getAllPlayers().contains(uniqueId)) {
                return squad;
            }
        }

        return null;
    }

    public List<Squad> getSquads() {
        return squads;
    }
}
