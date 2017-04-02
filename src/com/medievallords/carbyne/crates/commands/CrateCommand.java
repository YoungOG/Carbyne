package com.medievallords.carbyne.crates.commands;

import com.medievallords.carbyne.utils.Lang;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class CrateCommand extends BaseCommand {

    @Command(name = "crate", permission = "utils.commands.crate")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length == 0) {
            MessageManager.sendMessage(sender, Lang.CRATE_HELP.getAllMessages());
        }
    }
}
