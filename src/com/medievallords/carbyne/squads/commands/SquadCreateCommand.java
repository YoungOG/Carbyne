package com.medievallords.carbyne.squads.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-12.
 *
 */
public class SquadCreateCommand extends BaseCommand {

    @Command(name = "squad.create", inGameOnly = true, aliases = {"party.create"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /squad");
            return;
        }

        StaticClasses.squadManager.createSquad(player.getUniqueId());
    }
}
