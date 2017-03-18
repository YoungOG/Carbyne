package com.medievallords.carbyne.controlpoints.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-16.
 * for the Carbyne project.
 */
public class ControlCommand extends BaseCommand {

    @Command(name = "controlpoint", aliases = "cp", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /cp");
            return;
        }

        MessageManager.sendMessage(player, "&aCommands:");
        MessageManager.sendMessage(player, "&6/controlpoint list - Lists CPs");
        MessageManager.sendMessage(player, "&6/controlpoint create <name> <timeInSeconds> - Create a new controlpoint");
        MessageManager.sendMessage(player, "&6/controlpoint remove <name> - Remove a controlpoint");
        MessageManager.sendMessage(player, "&6/controlpoint timer - Show your current captured points remaining time");
        MessageManager.sendMessage(player, "&6/controlpoint reload - Reload the plugin");
        MessageManager.sendMessage(player, "&6/controlpoint teleport - Teleport to a controlpoint");
    }
}
