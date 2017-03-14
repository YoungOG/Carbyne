package com.medievallords.carbyne.parties.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 3/10/2017
 * for the Carbyne project.
 */
public class PartyCommand extends BaseCommand {


    @Command(name = "party", inGameOnly = true, aliases = {"p"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length == 0) {
            MessageManager.sendMessage(player, "&7======[ &bParty Help &7]======");
            MessageManager.sendMessage(player, "&a/party create &7- Create a new party.");
            MessageManager.sendMessage(player, "&a/party invite [player} &7- Invite a player to the party.");
            MessageManager.sendMessage(player, "&a/party join [player} &7- Joins a party.");
            MessageManager.sendMessage(player, "&a/party kick [player] &7- Kick a player from the party.");
            MessageManager.sendMessage(player, "&a/party leave &7- Leave the party.");
            MessageManager.sendMessage(player, "&a/party disband &7- Disband the party.");
            MessageManager.sendMessage(player, "&a/party list &7- View all parties.");
            MessageManager.sendMessage(player, "&a/party chat &7- Toggle your chat mode.");
            MessageManager.sendMessage(player, "&a/party chat [message] &7- Send a party message.");
            MessageManager.sendMessage(player, "&a/party friendlyfire &7- Toggle friendly fire.");
        }
    }
}
