package com.medievallords.carbyne.controlpoints.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-06-14
 * for the Carbyne project.
 */
public class ReloadControlPointCommand extends BaseCommand {

    @Command(name = "controlpoint.reload", aliases = {"cp.reload", "cp.r", "controlp.r", "controlpoint.r"}, permission = "carbyne.commands.controlpoints.reload", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: &6/controlpoint reload");
            return;
        }

        getControlPointManager().load();
        MessageManager.sendMessage(player, "&aControlpoint config saved and loaded");
    }
}
