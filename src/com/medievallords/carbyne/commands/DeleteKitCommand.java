package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.kits.Kit;
import com.medievallords.carbyne.kits.KitManager;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeleteKitCommand extends BaseCommand {

    private KitManager kitManager = StaticClasses.kitManager;

    @Command(name = "deletekit", aliases = {"delkit", "removekit"}, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        Kit kit = kitManager.getKit(args[0]);
        if (kit == null) {
            MessageManager.sendMessage(sender, "&cThe kit specified could not be found.");
            return;
        }

        kitManager.getKits().remove(kit);
        kitManager.getConfig().set("kits." + kit.getName(), null);

        try {
            kitManager.getConfig().save(kitManager.getFile());
            MessageManager.sendMessage(sender, "&cFailed to remove from kits.yml!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        MessageManager.sendMessage(sender, "&cSuccessfully removed the " + kit.getName() + " kit.");
    }
}
