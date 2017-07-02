package com.medievallords.carbyne.squads.commands;

import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.squads.SquadType;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-12
 * for the Carbyne project.
 */
public class SquadJoinCommand extends BaseCommand {

    @Command(name = "squad.join", inGameOnly = true, aliases = {"squad.j", "squad.accept", "squad.a"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (getSquadManager().getSquad(player.getUniqueId()) != null) {
            MessageManager.sendMessage(player, "&cYou are already in a squad.");
            return;
        }

        if (args.length == 0) {
            Squad squad = null;

            for (Squad parties : getSquadManager().getSquads()) {
                if (parties.getInvitedPlayers().contains(player.getUniqueId())) {
                    squad = parties;
                    break;
                }
            }

            if (squad == null) {
                MessageManager.sendMessage(player, "&cYou have not been invited to any squads.");
                return;
            }

            if (squad.getAllPlayers().size() >= 5) {
                MessageManager.sendMessage(player, "&cThat squad is currently full.");
                return;
            }

            squad.getInvitedPlayers().remove(player.getUniqueId());

            squad.sendAllMembersMessage("&b" + player.getName() + " &ahas joined the squad.");

            squad.getMembers().add(player.getUniqueId());

            MessageManager.sendMessage(player, "&aYou have joined the squad.");
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                MessageManager.sendMessage(player, "&cThat player could not be found.");
                return;
            }

            Squad squad = getSquadManager().getSquad(target.getUniqueId());

            if (squad == null) {
                MessageManager.sendMessage(player, "&cThat player is not in a squad.");
                return;
            }

            if (squad.getAllPlayers().size() >= 5) {
                MessageManager.sendMessage(player, "&cThat squad is currently full.");
                return;
            }

            if (squad.getType() == SquadType.PUBLIC) {
                squad.sendAllMembersMessage("&b" + player.getName() + " &ahas joined the squad.");

                squad.getMembers().add(player.getUniqueId());

                MessageManager.sendMessage(player, "&aYou have joined the squad.");
            } else {
                if (!squad.getInvitedPlayers().contains(player.getUniqueId())) {
                    MessageManager.sendMessage(player, "&cYou have not been invited to that squad.");
                    return;
                }

                squad.getInvitedPlayers().remove(player.getUniqueId());

                squad.sendAllMembersMessage("&b" + player.getName() + " &ahas joined the squad.");

                squad.getMembers().add(player.getUniqueId());

                MessageManager.sendMessage(player, "&aYou have joined the squad.");
            }
        } else {
            MessageManager.sendMessage(player, "&cUsage: /squad");
        }
    }
}
