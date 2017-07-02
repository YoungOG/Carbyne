package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

/**
 * Created by Calvin on 6/25/2017
 * for the Carbyne project.
 */
public class ResetPinCommand extends BaseCommand {

    @Command(name = "resetpin", permission = "carbyne.command.resetpin")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();

        if (commandArgs.getArgs().length != 1) {
            MessageManager.sendMessage(sender, "&cUsage: /resetpin <player>");
            return;
        }

        Profile profile = getProfileManager().getProfile(commandArgs.getArgs(0));

        if (profile == null) {
            MessageManager.sendMessage(sender, "&cThat player could not be found.");
            return;
        }

        profile.setPin("");

        MessageManager.sendMessage(sender, "&7You have reset the pin of &c" + commandArgs.getArgs(0) + "&7.");
    }
}
