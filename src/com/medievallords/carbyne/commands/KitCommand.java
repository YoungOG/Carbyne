package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.kits.Kit;
import com.medievallords.carbyne.kits.KitManager;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand extends BaseCommand {

    private KitManager kitManager = StaticClasses.kitManager;

    @Command(name = "kit", aliases = {"ekit", "kits"})
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();
        Player player = commandArgs.getPlayer();

        if (args.length == 0)
            kitManager.openKitMenuGui(player);
        else if (args.length == 1) {
            if (kitManager.getKit(args[0]) == null) {
                MessageManager.sendMessage(player, "&cCould not find the kit specified.");
                return;
            }

            Kit kit = kitManager.getKit(args[0]);

            if (!player.hasPermission("carbyne.kits." + kit.getName()) && !player.hasPermission("carbyne.kits.*")) {
                MessageManager.sendMessage(player, "&cYou do not have access to this kit.");
                return;
            }

            kit.apply(player);
        } else if (args.length == 2) {
            if (!player.hasPermission("carbyne.kits.give")) {
                MessageManager.sendMessage(player, "&cYou do not have permission to give kits.");
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageManager.sendMessage(sender, "&cThe player specified cannot be found.");
                return;
            }

            Kit kit = kitManager.getKit(args[1]);
            if (kit == null) {
                MessageManager.sendMessage(sender, "&cThe kit specified could not be found.");
                return;
            }

            kit.apply(target);
            MessageManager.sendMessage(player, "&7You have given &5" + target.getName() + " &7the &b" + kit.getName() + " &7kit.");
        }
    }
}
