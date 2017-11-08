package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Calvin on 8/2/2017
 * for the Carbyne project.
 */
public class StaffChatCommand extends BaseCommand {

    @Command(name = "staffchat", permission = "carbyne.staff", aliases = {"sc"})
    public void onCommand(CommandArgs commandArgs) {
        if (commandArgs.isPlayer()) {
            if (commandArgs.length() == 0) {
                if (!getStaffManager().getStaffChatPlayers().contains(commandArgs.getPlayer().getUniqueId())) {
                    getStaffManager().getStaffChatPlayers().add(commandArgs.getPlayer().getUniqueId());
                    MessageManager.sendMessage(commandArgs.getPlayer(), "&aYou have entered Staff Chat.");
                } else {
                    getStaffManager().getStaffChatPlayers().remove(commandArgs.getPlayer().getUniqueId());
                    MessageManager.sendMessage(commandArgs.getPlayer(), "&cYou have left Staff Chat.");
                }
            } else {
                MessageManager.sendStaffMessage(commandArgs.getSender(), StringUtils.join(commandArgs.getArgs(), " ", 0, commandArgs.getArgs().length));
            }
        } else {
            MessageManager.sendStaffMessage(commandArgs.getSender(), StringUtils.join(commandArgs.getArgs(), " ", 0, commandArgs.getArgs().length));
        }
    }
}
