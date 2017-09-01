package com.medievallords.carbyne.packages.commands;

import com.medievallords.carbyne.packages.Package;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-08-03.
 */
public class PackageGiveCommand extends BaseCommand {

    @Command(name = "package.give", aliases = {"p.give", "pack.give"}, permission = "carbyne.packages.admin")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender player = commandArgs.getSender();

        if (args.length != 3) {
            MessageManager.sendMessage(player, "&cUsage: &6/p give <player> <package> <amount>");
            return;
        }

        Player giveTo = Bukkit.getServer().getPlayer(args[0]);
        if (giveTo == null) {
            MessageManager.sendMessage(player, "&cCould not find that player");
            return;
        }

        String p = args[1];
        Package pack = Package.getPackage(p);
        if (pack == null) {
            MessageManager.sendMessage(player, "&cCould not find a package with that name");
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(player, "&cAmount must be a number");
            return;
        }

        if (giveTo.getInventory().firstEmpty() == -1) {
            giveTo.getWorld().dropItemNaturally(giveTo.getLocation(), pack.getItem(amount));
            MessageManager.sendMessage(player, "&a" + amount + " " + pack.getDisplayName() + "&b has been dropped to &7" + giveTo.getName());
        } else {
            giveTo.getInventory().addItem(pack.getItem(amount));
            MessageManager.sendMessage(player, "&a" + amount + " " + pack.getDisplayName() + "&b has been given to &7" + giveTo.getName());
        }
    }
}
