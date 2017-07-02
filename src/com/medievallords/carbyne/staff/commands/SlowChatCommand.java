package com.medievallords.carbyne.staff.commands;

import com.google.common.primitives.Ints;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class SlowChatCommand extends BaseCommand {

    @Command(name = "slowchat", permission = "carbyne.staff")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length == 0) {
            MessageManager.sendMessage(sender, "&cUsage: /slowchat <time|off>");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("off")) {
                if (getStaffManager().getSlowChatTime() <= 0) {
                    MessageManager.sendMessage(sender, "&cThe chat is now slowed.");
                    return;
                }

                MessageManager.broadcastMessage("&eThe chat is now longer slowed");
                getStaffManager().setSlowChatTime(0);

                for (UUID id : Cooldowns.cooldowns.rowKeySet()) {
                    if (Cooldowns.getCooldown(id, "slowChatCD") >= 0) {
                        Cooldowns.removeCooldowns(id);
                    }
                }
            } else {
                if (Ints.tryParse(args[0]) == null) {
                    MessageManager.sendMessage(sender, "&cYou must input a valid number.");
                    return;
                }

                if (Integer.valueOf(args[0]) <= 0) {
                    MessageManager.sendMessage(sender, "&cNumber must be more than zero.");
                    return;
                }

                getStaffManager().setSlowChatTime(Integer.valueOf(args[0]));
                MessageManager.broadcastMessage("&eThe chat is now slowed, you can only speak every &6" + Integer.valueOf(args[0]) + " &eseconds.");
            }
        } else {
            MessageManager.sendMessage(sender, "&cUsage: /slowchat <time|off>");
        }
    }
}
