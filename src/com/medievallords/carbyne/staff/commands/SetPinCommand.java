package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 6/25/2017
 * for the Carbyne project.
 */
public class SetPinCommand extends BaseCommand {

    @Command(name = "setpin", permission = "carbyne.command.setpin", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        Profile profile = getProfileManager().getProfile(player.getUniqueId());

        if (commandArgs.getArgs().length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /setpin <pin>");
            return;
        }

        if (profile == null) {
            MessageManager.sendMessage(player, "&cYour profile could not be accessed at this time.");
            return;
        }

        if (!isFourDigitCode(commandArgs.getArgs(0))) {
            MessageManager.sendMessage(player, "&7Your PIN must be &c4 &7numeric digits.");
            return;
        }

        profile.setPin(commandArgs.getArgs(0));

        MessageManager.sendMessage(player, "&7Your PIN has been set to &c" + profile.getPin() + "&7.");
    }

    public boolean isFourDigitCode(String string) {
        String regex = "[0-9]+";

        return string.length() == 4 && string.matches(regex);
    }
}
