package com.medievallords.carbyne.quests.commands;


import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class QuestReloadCommand extends BaseCommand {

    @Command(name = "reloadquest", permission = "carbyne.administrator")
    public void execute(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();

        StaticClasses.questHandler.reload();
        MessageManager.sendMessage(sender, "&aQuests has been reloaded.");
    }
}
