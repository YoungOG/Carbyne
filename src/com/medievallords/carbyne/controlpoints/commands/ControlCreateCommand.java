package com.medievallords.carbyne.controlpoints.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by Williams on 2017-03-16.
 * for the Carbyne project.
 */
public class ControlCreateCommand extends BaseCommand {

    @Command(name = "controlpoint.create", inGameOnly = true, permission = "carbyne.controlpoints.admin")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&cUsage: /cp");
            return;
        }

        try {
            int timer = Integer.parseInt(args[1]);
            Carbyne.getInstance().getControlManager().createControlPoint(player, player.getTargetBlock((Set<Material>) null, 5).getLocation(), args[0], timer);
        } catch (NumberFormatException exception) {
            MessageManager.sendMessage(player, "&cTimer can only be a number");
        }
    }
}
