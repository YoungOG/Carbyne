package com.medievallords.carbyne.economy.commands.player;

import com.medievallords.carbyne.economy.account.Account;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class WithdrawCommand extends BaseCommand {

    @Command(name = "withdraw", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /withdraw <amount>");
            return;
        }

        if (getMarketManager().isEconomyHalted()) {
            MessageManager.sendMessage(player, "&cThe economy is temporarily disabled. The administrators will let you know when it is re-enabled.");
            return;
        }

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
            MessageManager.sendMessage(player, "&cThis command can only be used in survival mode!");
            return;
        }

        int amount, balance = (int) Account.getAccount(player.getUniqueId()).getBalance();

        if (args[0].equalsIgnoreCase("all")) {
            amount = balance * getMarketManager().getGoldWorth();
        } else {
            try {
                amount = Integer.parseInt(args[0]) * getMarketManager().getGoldWorth();
            } catch (Exception e) {
                MessageManager.sendMessage(player, "&7You must enter a valid number.");
                return;
            }

            if (amount < 0) {
                MessageManager.sendMessage(player, "&7You cannot withdraw negative numbers.");
                return;
            }
        }

        if (amount == 0) {
            MessageManager.sendMessage(player, "&7You cannot withdraw zero credits.");
            return;
        }

        if (amount > balance) {
            MessageManager.sendMessage(player, "&7You do not have enough credits in your account.");
            return;
        }

        Map<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack(Material.GOLD_NUGGET, amount / getMarketManager().getGoldWorth()));

        if (!leftover.isEmpty()) {
            for (ItemStack item : leftover.values()) {
                amount -= item.getAmount();
            }
        }

        // 2nd check due to lack of inventory space.
        if (amount == 0) {
            MessageManager.sendMessage(player, "&7You do not have enough inventory space.");
            return;
        }

        getMarketManager().withdraw(player.getUniqueId(), amount);
        MessageManager.sendMessage(player, "&7You have withdrawn &c" + amount / getMarketManager().getGoldWorth() + " &7gold nuggets in exchange for &c" + (amount >= balance  ? "all" : "\u00A9" + amount) + " &7credits.");
    }
}