package com.medievallords.carbyne.gear.commands;

import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.StringUtils;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;


/**
 * Created by Williams on 2017-03-18.
 * for the Carbyne project.
 */
public class GearGiveCommand extends BaseCommand {

    @Command(name = "carbyne.give", aliases = {"cg.g", "carbyne.g", "cg.give"}, permission = "carbyne.gear.administrator")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length == 3) {

            Player toGive = Bukkit.getServer().getPlayer(args[0]);
            if (toGive == null) {
                MessageManager.sendMessage(sender, "&cCould not find that player.");
                return;
            }

            if (args[1].equalsIgnoreCase("prizeegg")) {
                handlePB(args, toGive);
                return;
            }

            CarbyneGear carbyneGear = StaticClasses.gearManager.getCarbyneGear(args[1]);
            if (carbyneGear == null) {
                MessageManager.sendMessage(sender, "&cCould not find CarbyneGear &5" + args[1]);
                return;
            }

            int amount = 0;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                MessageManager.sendMessage(sender, "&cAmount can only be a number!");
            }

            while (amount > 0) {
                toGive.getInventory().addItem(carbyneGear.getItem(false).clone());
                StringUtils.logToFile("[Carbyne] " + new Date().toString() + " --> " + toGive.getName() + " has been GIVEN a " + carbyneGear.getDisplayName(), "carbyneItemLog.txt");
                amount--;
            }
        }

        else if (args.length == 2) {
            Player toGive = Bukkit.getServer().getPlayer(args[0]);
            if (toGive == null) {
                MessageManager.sendMessage(sender, "&cCould not find that player.");
                return;
            }

            CarbyneGear carbyneGear = StaticClasses.gearManager.getCarbyneGear(args[1]);
            if (carbyneGear == null) {
                MessageManager.sendMessage(sender, "&cCould not find CarbyneGear &5" + args[1]);
                return;
            }

            toGive.getInventory().addItem(carbyneGear.getItem(false).clone());
            StringUtils.logToFile("[Carbyne] " + new Date().toString() + " --> " + toGive.getName() + " has been GIVEN a " + carbyneGear.getDisplayName(), "carbyneItemLog.txt");
        }

        else {
            MessageManager.sendMessage(sender, "&cUsage: /carbyne give <player> <gearCode> <amount>");
        }
    }

    private void handlePB(String[] args, Player player) {
        int amount = 0;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(player, "&cAmount can only be a number!");
        }

        while (amount > 0) {
            player.getInventory().addItem(StaticClasses.gearManager.getPrizeEggItem());
            amount--;
        }
    }
}
