package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;

public class ReloadMainConfigCommand extends BaseCommand {

    @Command(name = "cg.config.reload", permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Carbyne.getInstance().reloadConfig();
        Carbyne.getInstance().saveConfig();
        MessageManager.sendMessage(commandArgs.getSender(), "&aThe main config has been reloaded.");
    }
}
