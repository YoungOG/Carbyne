package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class MuteChatCommand extends BaseCommand {

    @Command(name = "mutechat", aliases = {"mc"}, permission = "carbyne.staff.mutechat")
    public void onCommand(CommandArgs commandArgs) {
        if (!getStaffManager().isChatMuted()) {
            MessageManager.broadcastMessage("&cThe chat has been muted.");
            getStaffManager().setChatMuted(true);
        } else {
            MessageManager.broadcastMessage("&aThe chat is no longer muted.");
            getStaffManager().setChatMuted(false);
        }
    }
}
