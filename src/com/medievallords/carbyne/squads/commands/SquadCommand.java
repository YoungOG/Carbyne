package com.medievallords.carbyne.squads.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 3/10/2017
 * for the Carbyne project.
 */
public class SquadCommand extends BaseCommand {


    @Command(name = "squad", inGameOnly = true, aliases = {"s"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length == 0) {
            MessageManager.sendMessage(player, "&7======[ &bSquad Help &7]======");
            MessageManager.sendMessage(player, "&a/squad create &7- Create a new squad.");
            MessageManager.sendMessage(player, "&a/squad invite [player} &7- Invite a player to the squad.");
            MessageManager.sendMessage(player, "&a/squad join [player} &7- Joins a squad.");
            MessageManager.sendMessage(player, "&a/squad kick [player] &7- Kick a player from the squad.");
            MessageManager.sendMessage(player, "&a/squad leave &7- Leave the squad.");
            MessageManager.sendMessage(player, "&a/squad disband &7- Disband the squad.");
            MessageManager.sendMessage(player, "&a/squad list &7- View all squads.");
            MessageManager.sendMessage(player, "&a/squad chat &7- Toggle your chat mode.");
            MessageManager.sendMessage(player, "&a/squad chat [message] &7- Send a squad message.");
            MessageManager.sendMessage(player, "&a/squad friendlyfire &7- Toggle friendly fire.");
        }
    }
}
