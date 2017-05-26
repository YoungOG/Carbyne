package com.medievallords.carbyne.economy.commands.player;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MarketBuyCommand extends BaseCommand {

    @Command(name = "buy", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 3) {
            MessageManager.sendMessage(player, "&c/buy <amount> <item> <priceLimit>");
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

        double priceLimit;

        try {
            priceLimit = Double.parseDouble(args[2]);

            if (priceLimit < 0) {
                MessageManager.sendMessage(player, "&7You must enter a positive price limit.");
                return;
            } else if (priceLimit == 0) {
                MessageManager.sendMessage(player, "&7You must enter a price limit greater than zero.");
                return;
            }
//            } else if (priceLimit > em.getBalance(player.getUniqueId())) {
//                MessageManager.sendMessage(player, "&7You do not have that much gold in your account.");
//                return;
//            }
        } catch (Exception e) {
            MessageManager.sendMessage(player ,"&7You must enter a valid price limit.");
            return;
        }

        getMarketManager().buy(player, itemStack, priceLimit);
    }
}
