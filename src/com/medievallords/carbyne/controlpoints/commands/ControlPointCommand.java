package com.medievallords.carbyne.controlpoints.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-06-22.
 */
public class ControlPointCommand extends BaseCommand {

    @Command(name = "controlpoint", aliases = {"cp", "controlp", "controlpoint"}, permission = "carbyne.commands.controlpoints", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        MessageManager.sendMessage(player, "&aControlPoint Commands");
        MessageManager.sendMessage(player, "&6/controlpoint create <name> <timer> optional:<displayName>");
        MessageManager.sendMessage(player, "&6/controlpoint remove <name>");
        MessageManager.sendMessage(player, "&6/controlpoint reload");
        MessageManager.sendMessage(player, "&6/controlpoint reward <add/remove> <controlPoint> <index/reward>");
        MessageManager.sendMessage(player, "&6/controlpoint list");
    }
}
