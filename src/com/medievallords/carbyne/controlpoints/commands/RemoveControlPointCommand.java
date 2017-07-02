package com.medievallords.carbyne.controlpoints.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-06-22.
 */
public class RemoveControlPointCommand extends BaseCommand {

    @Command(name = "controlpoint.remove", aliases = {"cp.remove", "cp.r", "controlp.r", "controlpoint.r"}, permission = "carbyne.commands.controlpoints.remove", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length > 1) {
            MessageManager.sendMessage(player, "&cUsage: &6/controlpoint remove <name>");
            return;
        }

        String toRemove = args[0];
        getControlPointManager().removeControlPoint(player, toRemove);
    }
}
