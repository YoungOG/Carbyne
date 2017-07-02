package com.medievallords.carbyne.duels.arena.commands;


import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-06-24
 * for the Carbyne project.
 */
public class ArenaReloadCommand extends BaseCommand {

    @Command(name = "arena.reload", aliases = {"arena.r", "arena.rel"}, permission = "carbyne.commands.arena")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        getDuelManager().loadArenas();
        MessageManager.sendMessage(player, "Reloaded");
    }
}
