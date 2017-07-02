package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class HelpopCommand extends BaseCommand {

    @Command(name = "helpop", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (commandArgs.length() < 1) {
            MessageManager.sendMessage(player, "&cUsage: /helpop <reason>");
            return;
        }

        String helpMessage = StringUtils.join(args, " ", 1, args.length);

        if (Cooldowns.tryCooldown(player.getUniqueId(), "helpopCD", 60000)) {
            MessageManager.sendMessage(player, "&aAll online staff have been alerted.");

            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (all.hasPermission("carbyne.staff")) {
                    MessageManager.sendMessage(all, "&6&m»----------------------------«");
                    JSONMessage message = JSONMessage.create(ChatColor.translateAlternateColorCodes('&',"&6&l" +player.getName() + " is requesting help."));
                    message.tooltip(ChatColor.translateAlternateColorCodes('&', "&aClick to teleport to " + player.getDisplayName() + "&a."));
                    message.runCommand("/teleport " + player.getName());
                    message.send(all);
                    MessageManager.sendMessage(all, "&6Message&b: " + helpMessage);
                    MessageManager.sendMessage(all, "&6&m»----------------------------«");
                }
            }
        } else {
            MessageManager.sendMessage(player, "&cYou are still on cooldown for another " + (Cooldowns.getCooldown(player.getUniqueId(), "helpopCD") / 1000) + " seconds.");
        }
    }
}
