package com.medievallords.carbyne.squads;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Calvin on 3/10/2017
 * for the Carbyne project.
 */

@Getter
@Setter
public class Squad {

    private SquadManager squadManager = Carbyne.getInstance().getSquadManager();
    private UUID uniqueId;
    private UUID leader;
    private SquadType type = SquadType.PRIVATE;
    private boolean friendlyFireToggled = false;
    private List<UUID> members = new ArrayList<>(), invitedPlayers = new ArrayList<>();
    private UUID targetUUID;
    private Squad targetSquad;

    public Squad(UUID leader) {
        this.uniqueId = UUID.randomUUID();
        this.leader = leader;
    }

    public void disbandParty(UUID leader) {
        if (!getLeader().equals(leader)) {
            MessageManager.sendMessage(leader, "&cOnly the leader can disband the squad.");
            return;
        }

        sendAllMembersMessage("&cYour squad has been disbanded.");

        squadManager.getSquads().remove(this);

        for (UUID id : getAllPlayers()) {
            Board board = Board.getByPlayer(Bukkit.getPlayer(Bukkit.getPlayer(id).getUniqueId()));

            if (board != null) {
                BoardCooldown targetCooldown = board.getCooldown("target");

                if (targetCooldown != null) {
                    targetCooldown.cancel();
                }
            }
        }
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
