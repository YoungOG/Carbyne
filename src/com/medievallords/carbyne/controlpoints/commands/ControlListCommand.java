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
public class ControlListCommand extends BaseCommand {

    @Command(name = "controlpoint.list", aliases = "l", permission = "carbyne.controlpoints.admin")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /cp");
            return;
        }

        String points = "";

        for (ControlPoint cp : Carbyne.getInstance().getControlManager().getControlPoints()) {
            points = points + "&6" + cp.getName() + "&7, ";
        }

        if (!Carbyne.getInstance().getControlManager().getControlPoints().isEmpty()) {
            MessageManager.sendMessage(player, "&aControl Points:");
            MessageManager.sendMessage(player, points);
            return;
        }
        MessageManager.sendMessage(player, "&cThere are no controlpoints avaliable.");
    }
}
