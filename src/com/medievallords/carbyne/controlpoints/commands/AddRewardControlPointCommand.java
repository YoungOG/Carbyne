package com.medievallords.carbyne.controlpoints.commands;

import com.medievallords.carbyne.controlpoints.ControlPoint;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by WE on 2017-06-22.
 */
public class AddRewardControlPointCommand extends BaseCommand {

    @Command(name = "controlpoint.reward", aliases = {"cp.reward", "cp.rw", "controlp.rw", "controlpoint.rw"}, permission = "carbyne.commands.controlpoints.reward", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length <= 2) {
            MessageManager.sendMessage(player,"&cUsage: &6/controlpoint reward <add/remove> <controlpoint> <index/reward>");
            return;
        }

        ControlPoint controlPoint = getControlPointManager().getControlPoint(args[1]);
        if (controlPoint == null) {
            MessageManager.sendMessage(player, "&cCouldn't find a controlpoint with that name");
            return;
        }

        List<String> rewards = controlPoint.getRewards();

        if (args[0].equalsIgnoreCase("add")) {
            String reward = "";

            for (int i = 2; i < args.length; i++) {
                reward = reward + args[i] + " ";
            }

            rewards.add(reward);
            controlPoint.rewards = rewards;
            getCarbyne().getControlPointsFileConfiguration().set("ControlPoints." + controlPoint.getName() + ".Rewards", controlPoint.getRewards());
            getControlPointManager().saveAndLoadConfig();
            MessageManager.sendMessage(player, "&aReward added: &b" + reward);

        } else if (args[0].equalsIgnoreCase("remove")) {
            try {

                rewards.remove(Integer.parseInt(args[2]));
                controlPoint.rewards = rewards;
                getCarbyne().getControlPointsFileConfiguration().set("ControlPoints." + controlPoint.getName() + ".Rewards", controlPoint.getRewards());
                getControlPointManager().saveAndLoadConfig();
                MessageManager.sendMessage(player, "Reward removed at index: " + args[2]);
            } catch (NumberFormatException e) {
                MessageManager.sendMessage(player, "&cIndex needs to be an integer");
            }


        } else {
            MessageManager.sendMessage(player,"&cUsage: &6/controlpoint reward <add/remove> <controlpoint> <index/reward>");
        }
    }
}
