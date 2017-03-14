package com.medievallords.carbyne.parties.commands;

import com.medievallords.carbyne.parties.Party;
import com.medievallords.carbyne.parties.PartyType;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-13.
 * for the Carbyne project.
 */
public class PartySetCommand extends BaseCommand{

    @Command(name = "party.set", inGameOnly = true, aliases = {"s"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Party party = getPartyManager().getParty(player.getUniqueId());

        if (args.length < 0 || args.length > 1) {
            MessageManager.sendMessage(player, "&cUsage: /party");
            return;
        }

        if (args.length == 0) {
            if (party.getType() == PartyType.PRIVATE) {
                party.setType(PartyType.PUBLIC);
                MessageManager.sendMessage(player, "&aThe party has been set to &b" + PartyType.PUBLIC.toString().toLowerCase().substring(0, 1).toUpperCase() + args[0].toLowerCase().substring(1) + "&a.");
            } else {
                party.setType(PartyType.PRIVATE);
                MessageManager.sendMessage(player, "&aThe party has been set to &b" + PartyType.PRIVATE.toString().toLowerCase().substring(0, 1).toUpperCase() + args[0].toLowerCase().substring(1) + "&a.");
            }
        }

        if (args.length == 1) {
            try {
                party.setType(PartyType.valueOf(args[0].toUpperCase()));
                MessageManager.sendMessage(player, "&aThe party has been set to &b" + args[0].toLowerCase().substring(0, 1).toUpperCase() + args[0].toLowerCase().substring(1) + "&a.");
            } catch (Exception exception) {
                MessageManager.sendMessage(player, "&cYou can only set the party to public or private.");
            }
        }
    }
}
