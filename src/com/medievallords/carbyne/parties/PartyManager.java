package com.medievallords.carbyne.parties;

import com.medievallords.carbyne.utils.MessageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Calvin on 3/10/2017
 * for the Carbyne project.
 */
public class PartyManager {

    private List<Party> parties = new ArrayList<>();

    public void createParty(UUID leader) {
        if (getParty(leader) != null) {
            MessageManager.sendMessage(leader, "&cYou are already in a party.");
            return;
        }

        Party party = new Party(leader);
        parties.add(party);

        MessageManager.sendMessage(leader, "&aYou have created a new party.\n&eUse &a/party &eto view all available party commands.");
    }

    public Party getParty(UUID uniqueId) {
        for (Party party : getParties()) {
            if (party.getAllPlayers().contains(uniqueId)) {
                return party;
            }
        }

        return null;
    }

    public List<Party> getParties() {
        return parties;
    }
}
