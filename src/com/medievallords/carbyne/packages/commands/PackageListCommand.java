package com.medievallords.carbyne.packages.commands;

import com.medievallords.carbyne.packages.Package;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

/**
 * Created by WE on 2017-08-05.
 */
public class PackageListCommand extends BaseCommand {

    @Command(name = "package.list", aliases = {"p.list", "pack.list"}, permission = "carbyne.packages.admin")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender player = commandArgs.getSender();

        if (Package.packages.isEmpty()) {
            MessageManager.sendMessage(player, "&cThere are no packages");
        }

        for (Package pack : Package.packages) {
            MessageManager.sendMessage(player, "&a" + pack.getName());
        }

    }
}
