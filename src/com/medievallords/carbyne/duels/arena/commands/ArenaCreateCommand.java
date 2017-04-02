package com.medievallords.carbyne.duels.arena.commands;

import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 3/17/2017
 * for the Carbyne project.
 */
public class ArenaCreateCommand extends BaseCommand {

    @Command(name = "arena.create", aliases = {"arena.c"}, permission = "carbyne.commands.arena")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /arena");
            return;
        }

        String arenaId = args[0];

        if (getDuelManager().getArena(arenaId) != null) {
            MessageManager.sendMessage(player, "&cAn arena already exists using that name.");
            return;
        }

        getDuelManager().getArenas().add(new Arena(arenaId));
        MessageManager.sendMessage(player, "&aYou have created a new arena with the ID \'&b" + arenaId + "&a\'.");
    }
}
