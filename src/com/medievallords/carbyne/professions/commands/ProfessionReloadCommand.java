package com.medievallords.carbyne.professions.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

/**
 * Created by Williams on 2017-08-22
 * for the Carbyne project.
 */
public class ProfessionReloadCommand extends BaseCommand {

    @Command(name = "profession.reload", aliases = {"prof.reload"}, permission = "carbyne.professions.admin")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        getCarbyne().getProfessionManager().reload();
        MessageManager.sendMessage(sender, "&cConfiguration reloaded");
    }
}
