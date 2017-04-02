package com.medievallords.carbyne.duels.arena.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 3/17/2017
 * for the Carbyne project.
 */
public class ArenaCommand extends BaseCommand {

    @Command(name = "arena", permission = "carbyne.commands.arena")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length == 0) {
            MessageManager.sendMessage(player, "&7========[ &bArena Commands &7]========");
            MessageManager.sendMessage(player, "&a/arena create &b<name> &7- Creates an arena.");
            MessageManager.sendMessage(player, "&a/arena setLobby &b<name> &7- Set the arena lobby.");
            MessageManager.sendMessage(player, "&a/arena addPedastool &b<name> &7- Adds a pedastool to the arena.");
            MessageManager.sendMessage(player, "&a/arena setSpawn &b<1/2> &b<name> &7- Adds a spawn point to the arena.");
            MessageManager.sendMessage(player, "&a/arena remove &b<name> &7- Removes an arena.");
            MessageManager.sendMessage(player, "&a/arena list &7- Lists all available arenas.");
        }
    }
}
