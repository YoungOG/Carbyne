package com.medievallords.carbyne.parties.commands;

import com.medievallords.carbyne.parties.Party;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by xwiena22 on 2017-03-13
 * for the Carbyne project.
 */
public class PartyLeaveCommand extends BaseCommand {

    @Command(name = "party.leave", inGameOnly = true, aliases = {"l"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /party");
            return;
        }

        Party party = getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            MessageManager.sendMessage(player, "&cYou are not in a party.");
            return;
        }

        if (party.getLeader().equals(player.getUniqueId())) {
            if (party.getMembers().size() > 0) {
                party.setLeader(party.getMembers().get(0));
                party.getMembers().remove(party.getMembers().get(0));

                MessageManager.sendMessage(player, "&cYou have left the party.");
                party.sendAllMembersMessage("&b" + player.getName() + " &chas left the party.");
                MessageManager.sendMessage(party.getLeader(), "&aThe previous party leader has left. You are the now the new party leader.");
            } else {
                party.disbandParty(player.getUniqueId());
            }

            return;
        }

        party.getMembers().remove(player.getUniqueId());

        MessageManager.sendMessage(player, "&cYou have left the party.");

        party.sendAllMembersMessage("&b" + player.getName() + " &chas left the party.");
    }
}
