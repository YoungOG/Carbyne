package com.medievallords.carbyne.announcer;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;

public class AnnouncerReloadCommand extends BaseCommand {

    @Command(name = "announcer.reload", permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {

        StaticClasses.announcerManager.reload(Carbyne.getInstance());
        MessageManager.sendMessage(commandArgs.getSender(), "&aAnnouncements have been reloaded.");
    }
}
