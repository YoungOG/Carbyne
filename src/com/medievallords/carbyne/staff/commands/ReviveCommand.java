package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import com.medievallords.carbyne.utils.serialization.InventorySerialization;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class ReviveCommand extends BaseCommand {

    @Command(name = "revive", aliases = {"restoreinv"}, permission = "carbyne.staff.revive")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&cUsage: /revive <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            MessageManager.sendMessage(sender, "&cThat player could not be found.");
            return;
        }

        Profile profile = getProfileManager().getProfile(target.getUniqueId());

        if (profile == null) {
            MessageManager.sendMessage(sender, "&cUnable to access that players profile.");
            return;
        }

        if (profile.getPreviousInventoryContentString().isEmpty()) {
            MessageManager.sendMessage(sender, "&cThere is no previous inventory for that player.");
            return;
        }

        InventorySerialization.setPlayerInventory(target, profile.getPreviousInventoryContentString());

        MessageManager.sendMessage(sender, "&aSuccessfully revived &5" + target.getName() + "&a.");
    }
}
