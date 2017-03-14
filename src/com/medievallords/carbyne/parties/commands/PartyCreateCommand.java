package com.medievallords.carbyne.parties.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-12.
 */
public class PartyCreateCommand extends BaseCommand {

    @Command(name = "party.create", inGameOnly = true, aliases = {"c"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /party");
            return;
        }

        getPartyManager().createParty(player.getUniqueId());
    }
}
