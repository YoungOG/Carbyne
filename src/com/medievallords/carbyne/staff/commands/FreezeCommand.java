package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 6/23/2017
 * for the Carbyne project.
 */
public class FreezeCommand extends BaseCommand {

    @Command(name = "freeze", permission = "carbyne.command.freeze")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&cUsage: /freeze <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            MessageManager.sendMessage(sender, "&cCould not find the player specified.");
            return;
        }

        if (!getStaffManager().getFrozen().contains(target.getUniqueId())) {
            MessageManager.sendMessage(sender, "&aYou have frozen &5" + target.getName() + "&a.");

            getStaffManager().freezePlayer(target);
            return;
        }

        if (getStaffManager().getFrozen().contains(target.getUniqueId())) {
            MessageManager.sendMessage(sender, "&aYou have un-frozen &5" + target.getName() + "&a.");

            getStaffManager().unfreezePlayer(target);
        }
    }
}
