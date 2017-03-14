package com.medievallords.carbyne.parties.commands;

import com.medievallords.carbyne.parties.Party;
import com.medievallords.carbyne.parties.PartyType;
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
public class PartyJoinCommand extends BaseCommand {

    @Command(name = "party.join", inGameOnly = true, aliases = {"j", "accept", "a"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (getPartyManager().getParty(player.getUniqueId()) != null) {
            MessageManager.sendMessage(player, "&cYou are already in a party.");
            return;
        }

        if (args.length == 0) {
            Party party = null;

            for (Party parties : getPartyManager().getParties()) {
                if (parties.getInvitedPlayers().contains(player.getUniqueId())) {
                    party = parties;
                    break;
                }
            }

            if (party == null) {
                MessageManager.sendMessage(player, "&cYou have not been invited to any parties.");
                return;
            }

            party.getInvitedPlayers().remove(player.getUniqueId());

            party.sendAllMembersMessage("&b" + player.getName() + " &ahas joined the party.");

            party.getMembers().add(player.getUniqueId());

            MessageManager.sendMessage(player, "&aYou have joined the party.");
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                MessageManager.sendMessage(player, "&cThat player could not be found.");
                return;
            }

            Party party = getPartyManager().getParty(target.getUniqueId());

            if (party == null) {
                MessageManager.sendMessage(player, "&cThat player is not in a party.");
                return;
            }

            if (party.getType() == PartyType.PUBLIC) {
                party.sendAllMembersMessage("&b" + player.getName() + " &ahas joined the party.");

                party.getMembers().add(player.getUniqueId());

                MessageManager.sendMessage(player, "&aYou have joined the party.");
            } else {
                if (!party.getInvitedPlayers().contains(player.getUniqueId())) {
                    MessageManager.sendMessage(player, "&cYou have not been invited to that party.");
                    return;
                }

                party.getInvitedPlayers().remove(player.getUniqueId());

                party.sendAllMembersMessage("&b" + player.getName() + " &ahas joined the party.");

                party.getMembers().add(player.getUniqueId());

                MessageManager.sendMessage(player, "&aYou have joined the party.");
            }
        } else {
            MessageManager.sendMessage(player, "&cUsage: /party");
        }
    }
}
