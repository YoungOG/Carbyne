package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class ReportCommand extends BaseCommand {

    @Command(name = "report", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (commandArgs.length() < 2) {
            MessageManager.sendMessage(player, "&cUsage: /report <player> <reason>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        String reportMessage = StringUtils.join(args, " ", 1, args.length);

        if (target == null) {
            MessageManager.sendMessage(player, "&cThat player is not online.");
            return;
        }

        if (Cooldowns.tryCooldown(player.getUniqueId(), "reportCD", 45000)) {
            MessageManager.sendMessage(player, "&aThanks for your report.");

            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (all.hasPermission("carbyne.staff")) {
                    JSONMessage message = JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&7[&bReport&7] &6" + player.getName() + " &chas reported &6" + target.getName() + " &cfor &e" + reportMessage + "&c."));
                    message.tooltip(ChatColor.translateAlternateColorCodes('&', "&aClick to teleport to " + target.getDisplayName() + "&a."));
                    message.runCommand("/teleport " + target.getName());
                    message.send(all);
                }
            }
        } else {
            MessageManager.sendMessage(player, "&cYou are still on cooldown for another " + (Cooldowns.getCooldown(player.getUniqueId(), "reportCD") / 1000) + " seconds.");
        }
    }
}
