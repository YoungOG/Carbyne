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
public class PartyDisbandCommand extends BaseCommand {

    @Command(name = "party.disband", inGameOnly = true, aliases = {"d"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Party party = getPartyManager().getParty(player.getUniqueId());

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /party");
            return;
        }

        if (party == null) {
            MessageManager.sendMessage(player, "&cYou are not in a party.");
            return;
        }

        party.disbandParty(player.getUniqueId());
    }
}
