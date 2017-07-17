package com.medievallords.carbyne.economy.commands.player;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MarketSellCommand extends BaseCommand {

    @Command(name = "sell", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 3) {
            MessageManager.sendMessage(player, "&c/sell <amount> <item> <price>");
            return;
        }

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
            MessageManager.sendMessage(player, "&cThis command can only be used in survival mode!");
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

        double price;

        try {
            price = Double.parseDouble(args[2]);

            if (price < 0) {
                MessageManager.sendMessage(player, "&7You must enter a positive price.");
                return;
            } else if (price == 0) {
                MessageManager.sendMessage(player, "&7You must enter a price greater than zero.");
                return;
            }
        } catch (Exception e) {
            MessageManager.sendMessage(player, "&7You must enter a valid price.");
            return;
        }

        getMarketManager().sell(player, itemStack, price);
    }
}
