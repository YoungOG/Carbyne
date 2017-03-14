package com.medievallords.carbyne.parties;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Calvin on 3/10/2017
 * for the Carbyne project.
 */

@Getter
@Setter
public class Party {

    private PartyManager partyManager = Carbyne.getInstance().getPartyManager();
    private UUID uniqueId;
    private UUID leader;
    private PartyType type = PartyType.PRIVATE;
    private boolean hidden, friendlyFireToggled = false;
    private List<UUID> members = new ArrayList<>(), invitedPlayers = new ArrayList<>();
    private int size = 5;
    //private List<TeamPlayer> moderators = new ArrayList<>();
    //private List<Stats> summedStats = new ArrayList<>();
    //private ChatColor color = ChatColor.RED;

    public Party(UUID leader) {
        this.uniqueId = UUID.randomUUID();
        this.leader = leader;
    }

    public void disbandParty(UUID leader) {
        if (!getLeader().equals(leader)) {
            MessageManager.sendMessage(leader, "&cOnly the leader can disband the party.");
            return;
        }

        sendAllMembersMessage("&cYour party has been disbanded.");

        partyManager.getParties().remove(this);
    }

    public ArrayList<UUID> getAllPlayers() {
        ArrayList<UUID> allPlayers = new ArrayList<>();
        allPlayers.add(leader);

        if (members != null && members.size() > 0)
            allPlayers.addAll(members);

        return allPlayers;
    }

    public void sendMembersMessage(String message) {
        for (UUID id : members) {
            MessageManager.sendMessage(id, message);
        }
    }

    public void sendAllMembersMessage(String message) {
        for (UUID id : getAllPlayers()) {
            MessageManager.sendMessage(id, message);
        }
    }
}
