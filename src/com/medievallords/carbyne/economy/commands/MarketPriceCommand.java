package com.medievallords.carbyne.economy.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MarketPriceCommand extends BaseCommand {

    @Command(name = "price", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&c/price <amount> <item>");
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[0]);

            if (amount < 0) {
                MessageManager.sendMessage(player, "&7You must enter a positive amount.");
                return;
            } else if (amount == 0) {
                MessageManager.sendMessage(player, "&7You must enter an amount greater than zero.");
                return;
            }
        } catch (Exception e) {
            MessageManager.sendMessage(player, "&7You must enter a valid amount.");
            return;
        }

        ItemStack itemStack;

        try {
            itemStack = getMarketManager().getItemStack(args[1], amount);
        } catch (Exception e) {
            MessageManager.sendMessage(player, "&7That is not a valid item.");
            return;
        }

        getMarketManager().showPrice(player, itemStack);
    }
}
