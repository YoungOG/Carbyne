package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class ImageReloadCommand extends BaseCommand {

    @Command(name = "reloadimage", permission = "carbyne.administrator")
    public void a(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();

//        getStaffManager().reloadImages(sender);
    }
}
