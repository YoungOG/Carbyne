package com.medievallords.carbyne.squads;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.nisovin.magicspells.events.SpellTargetEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Calvin on 3/10/2017
 * for the Carbyne project.
 */
public class SquadManager implements Listener {

    private List<Squad> squads = new ArrayList<>();

    public SquadManager() {
        Bukkit.getPluginManager().registerEvents(this, Carbyne.getInstance());
    }

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

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Squad squad = Carbyne.getInstance().getSquadManager().getSquad(player.getUniqueId());

        if (squad != null) {
            if (squad.getLeader().equals(player.getUniqueId())) {
                if (squad.getMembers().size() > 0) {
                    squad.setLeader(squad.getMembers().get(0));
                    squad.getMembers().remove(squad.getMembers().get(0));

                    squad.sendAllMembersMessage("&b" + player.getName() + " &chas left the squad.");
                    MessageManager.sendMessage(squad.getLeader(), "&aThe previous squad leader has left. You are the now the new squad leader.");
                } else {
                    squad.disbandParty(player.getUniqueId());
                }

                return;
            }

            squad.getMembers().remove(player.getUniqueId());

            squad.sendAllMembersMessage("&b" + player.getName() + " &chas left the squad.");
        }
    }

    @EventHandler
    public void onSpellTarget(SpellTargetEvent e)
    {
        if(e.getTarget() instanceof Player) {
            Player target = (Player) e.getTarget();
            Squad squad1 = Carbyne.getInstance().getSquadManager().getSquad(target.getUniqueId());

            if(squad1 != null && squad1.getMembers().contains(e.getCaster().getUniqueId()))
                e.setCancelled(true);
        }
    }

    public List<Squad> getSquads() {
        return squads;
    }
}
