package com.medievallords.carbyne.controlpoints.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.controlpoints.ControlPoint;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-16.
 * for the Carbyne project.
 */
public class ControlTeleportCommand extends BaseCommand {

    @Command(name = "controlpoint.teleport", aliases = "tp", inGameOnly = true, permission = "carbyne.controlpoins.admin")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /cp");
            return;
        }

        ControlPoint controlPoint = Carbyne.getInstance().getControlManager().getControlPoint(args[0]);
        if (controlPoint == null) {
            MessageManager.sendMessage(player, "&cCould not find controlpoint &9" + args[0]);
            return;
        }

        player.teleport(controlPoint.getLocation());
        MessageManager.sendMessage(player, "&aYou have teleported to &9" + controlPoint.getName());
    }
}
