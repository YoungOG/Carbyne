package com.medievallords.carbyne.economy.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DepositCommand extends BaseCommand {

    @Command(name = "deposit", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /deposit <amount>");
            return;
        }

        if (getEconomyManager().isEconomyHalted()) {
            MessageManager.sendMessage(player, "&cThe economy is temporarily disabled. The administrators will let you know when it is re-enabled.");
            return;
        }

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
            MessageManager.sendMessage(player, "&cThis command can only be used in survival mode!");
            return;
        }

        int amount, price, total = 0;

        boolean attemptDupe = false;
        ItemStack itemStack = null;

        for (ItemStack item : player.getInventory().all(Material.GOLD_NUGGET).values()) {
            if (!item.hasItemMeta() && !(item.getItemMeta().hasDisplayName() || item.getItemMeta().hasLore())) {
                total += item.getAmount();
            } else {
                attemptDupe = true;
                itemStack = item;
            }
        }

        if (attemptDupe) {
            MessageManager.sendMessage(player, "&7You cannot sell renamed gold nuggets.");

            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (all.hasPermission("carbyne.notify")) {
                    MessageManager.sendMessage(all, "&c[&4Dupe Attempt&c]: " + player.getName() + " attempted to dupe gold.");
                    MessageManager.sendMessage(all, "&cDisplayName: " + itemStack.getItemMeta().getDisplayName());
                }
            }
        }

        if (args[0].equalsIgnoreCase("all")) {
            amount = total;
        } else {
            try {
                amount = Integer.parseInt(args[0]);
            } catch (Exception e) {
                MessageManager.sendMessage(player, "&7You must enter a valid number.");
                return;
            }

            if (amount < 0) {
                MessageManager.sendMessage(player, "&7You cannot deposit negative numbers.");
                return;
            }
        }

        if (amount == 0) {
            MessageManager.sendMessage(player, "&7You cannot deposit zero gold.");
            return;
        }

        if (amount > total) {
            MessageManager.sendMessage(player, "&7You do not have that much gold in your inventory.");
            return;
        }

        price = amount * getEconomyManager().getGoldWorth();

        getEconomyManager().deposit(player.getUniqueId(), price);
        player.getInventory().removeItem(new ItemStack(Material.GOLD_NUGGET, amount));
        MessageManager.sendMessage(player, "&7You have deposited &c" + (amount == total ? "all" : amount) + " &7gold nuggets in your account for &c\u00A9" + price + " &7credits.");
    }
}