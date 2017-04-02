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
public class ArenaRemoveSpawnCommand extends BaseCommand {

    @Command(name = "arena.removespawn", aliases = {"arena.delspawn", "arena.deletespawn"}, permission = "carbyne.commands.arena")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&cUsage: /arena");
            return;
        }

        Arena arena = getDuelManager().getArena(args[1]);

        if (arena == null) {
            MessageManager.sendMessage(player, "&cCould not find an arena with the ID \'" + args[1] + "\'.");
            return;
        }

        try {
            int i = Integer.parseInt(args[0]);
            
            if (i == 1) {
                if (arena.getSpawnPointLocations()[0] == null) {
                    MessageManager.sendMessage(player, "&cThere is not a spawn point for the arena &b\'" + arena.getArenaId() + "&a\'.");
                    return;
                }

                arena.getSpawnPointLocations()[0] = null;
                MessageManager.sendMessage(player, "&aYou have removed spawn point 1 for the arena \'&b" + arena.getArenaId() + "&a\'.");
            } else if (i == 2) {
                if (arena.getSpawnPointLocations()[1] == null) {
                    MessageManager.sendMessage(player, "&cThere is not a spawn point for the arena &b\'" + arena.getArenaId() + "&a\'.");
                    return;
                }

                arena.getSpawnPointLocations()[1] = null;
                MessageManager.sendMessage(player, "&aYou have removed spawn point 2 for the arena \'&b" + arena.getArenaId() + "&a\'.");
            } else {
                MessageManager.sendMessage(player, "&cYou must input either 1 or 2 for the spawn point.");
            }

            arena.save();
        } catch (NumberFormatException ignored) {
            MessageManager.sendMessage(player, "&cYou must input either 1 or 2 for the spawn point.");
        }
    }
}
