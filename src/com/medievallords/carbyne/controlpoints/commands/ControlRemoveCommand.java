package com.medievallords.carbyne.controlpoints.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-16.
 * for the Carbyne project.
 */
public class ControlRemoveCommand extends BaseCommand {

    @Command(name = "controlpoint.remove", aliases = "rm", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /cp");
            return;
        }

        Carbyne.getInstance().getControlManager().removeControlPoint(args[0], player);
    }
}
