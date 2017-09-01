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

    @Command(name = "staffchat", permission = "carbyne.staff")
    public void onCommand(CommandArgs commandArgs) {
        MessageManager.sendStaffMessage(commandArgs.getSender(), StringUtils.join(commandArgs.getArgs(), " ", 0, commandArgs.getArgs().length));
    }
}
