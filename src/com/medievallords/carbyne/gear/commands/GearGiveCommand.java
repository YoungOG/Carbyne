package com.medievallords.carbyne.gear.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


/**
 * Created by Williams on 2017-03-18.
 * for the Carbyne project.
 */
public class GearGiveCommand extends BaseCommand {

    @Command(name = "carbyne.give", aliases = {"cg.g", "carbyne.g"},inGameOnly = true, permission = "carbyne.gear.admin")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length == 3) {

            Player toGive = Bukkit.getServer().getPlayer(args[0]);
            if (toGive == null) {
                MessageManager.sendMessage(player, "&cCould not find that player.");
                return;
            }

            CarbyneGear carbyneGear = Carbyne.getInstance().getGearManager().getCarbyneGear(args[1]);
            if (carbyneGear == null) {
                MessageManager.sendMessage(player, "&cCould not find CarbyneGear &5" + args[1]);
                return;
            }

            int amount = 0;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                MessageManager.sendMessage(player, "&cAmount can only be a number!");
            }

            while (amount > 0) {
                toGive.getInventory().addItem(carbyneGear.getItem(false).clone());
                amount--;
            }
        }

        else if (args.length == 2) {
            Player toGive = Bukkit.getServer().getPlayer(args[0]);
            if (toGive == null) {
                MessageManager.sendMessage(player, "&cCould not find that player.");
                return;
            }

            CarbyneGear carbyneGear = Carbyne.getInstance().getGearManager().getCarbyneGear(args[1]);
            if (carbyneGear == null) {
                MessageManager.sendMessage(player, "&cCould not find CarbyneGear &5" + args[1]);
                return;
            }

            toGive.getInventory().addItem(carbyneGear.getItem(false).clone());
        }

        else {
            MessageManager.sendMessage(player, "&cUsage: /carbyne give <player> <gearCode> <amount>");
        }
    }
}
