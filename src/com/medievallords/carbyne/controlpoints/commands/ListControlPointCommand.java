package com.medievallords.carbyne.controlpoints.commands;

import com.medievallords.carbyne.controlpoints.ControlPoint;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-06-22.
 */
public class ListControlPointCommand extends BaseCommand {

    @Command(name = "controlpoint.list", aliases = {"cp.list", "cp.rw", "controlp.l", "controlpoint.l"}, permission = "carbyne.commands.controlpoints.reward", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (getControlPointManager().controlPoints.isEmpty()) {
            MessageManager.sendMessage(player, "&cThere are no control points");
            return;
        }

        MessageManager.sendMessage(player, "&aControlpoints:");

        String cps = "";

        for (int i = 0; i < getControlPointManager().controlPoints.size(); i++) {
            ControlPoint controlPoint = getControlPointManager().controlPoints.get(i);
            cps = cps + "&a" + controlPoint.getName() + "&6, ";
        }

        MessageManager.sendMessage(player, cps);
    }
}
