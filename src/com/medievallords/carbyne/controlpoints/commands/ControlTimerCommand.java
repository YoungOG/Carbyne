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
public class ControlTimerCommand extends BaseCommand {

    @Command(name = "controlpoint.timer", aliases = "t", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /cp");
            return;
        }

        ControlPoint cp = Carbyne.getInstance().getControlManager().getControlPoint(player);
        if (cp == null) {
            MessageManager.sendMessage(player, "&cYou are currently not capturing a point");
            return;
        }


        int total = Carbyne.getInstance().getTimerListener().getCapTimer().get(cp);
        int minutes = total / 60;
        int seconds = total - minutes * 60;
        MessageManager.sendMessage(player, "&aTime remaining: &d" + minutes + "&7:&d" + seconds);
    }
}
