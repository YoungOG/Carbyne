package com.medievallords.carbyne.economy.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 2/11/2017
 * for the Carbyne project.
 */
public class MarketCommand extends BaseCommand {

    @Command(name = "market", aliases = { "marketplace", "mp" }, inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            if (getMarketManager().isEconomyHalted()) {
                MessageManager.sendMessage(player, "Error: &cThe economy has been halted.");
                return;
            }

            if (args[0].equalsIgnoreCase("sell")) {

            }
        } else {
            MessageManager.sendMessage(player, "&c/market");
        }
    }
}