package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class StaffCommand extends BaseCommand {

    @Command(name = "staff", aliases = {"modmode"}, inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length == 0) {
            if (player.hasPermission("carbyne.staff.staffmode"))
                getStaffManager().toggleStaffMode(player);
        } else if (args.length == 1) {
            if (!player.hasPermission("carbyne.staff.staffmode.others")) {
                MessageManager.sendMessage(player, "&cYou do not have sufficient permissions.");
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                MessageManager.sendMessage(player, "&cThat player could not be found.");
                return;
            }

            if (target.hasPermission("carbyne.staff.staffmode")) {
                getStaffManager().toggleStaffMode(target);
                MessageManager.sendMessage(player, "&aYou have toggled &5" + target.getName() + " &ain or out of staff mode.");
            } else {
                MessageManager.sendMessage(player, "&cThis player does not have sufficient permissions.");
            }
        } else {
            MessageManager.sendMessage(player, "&cUsage: /vanish <player>");
        }
    }
}
