package com.medievallords.carbyne.economy.commands;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.types.DepositMoneyTask;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class DepositCommand extends BaseCommand {

    @Command(name = "deposit", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length < 1 || args.length > 3) {
            MessageManager.sendMessage(player, "&cUsage: /deposit <all/nugget/ingot/block> [amount]");
            return;
        }

        if (StaticClasses.economyManager.isEconomyHalted()) {
            MessageManager.sendMessage(player, "&cThe economy is temporarily disabled. The administrators will let you know when it is re-enabled.");
            return;
        }

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
            MessageManager.sendMessage(player, "&cThis command can only be used in survival mode!");
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("all")) {
                DepositObject nuggets = sellNuggets("all", player);
                DepositObject ingots = sellIngots("all", player);
                DepositObject blocks = sellBlocks("all", player);

                if (nuggets == null || ingots == null || blocks == null)
                    return;

                int totalPrice = nuggets.getPrice() + ingots.getPrice() + blocks.getPrice();

                StaticClasses.economyManager.deposit(player.getUniqueId(), totalPrice);
                handleEvent(player.getUniqueId(), totalPrice);
                PlayerUtility.removeItems(player.getInventory(), new ItemStack(Material.GOLD_NUGGET), nuggets.getAmount());
                PlayerUtility.removeItems(player.getInventory(), new ItemStack(Material.GOLD_INGOT), ingots.getAmount());
                PlayerUtility.removeItems(player.getInventory(), new ItemStack(Material.GOLD_BLOCK), blocks.getAmount());
                player.updateInventory();
                MessageManager.sendMessage(player, "&7You have deposited &call &7gold in your account for &c\u00A9" + totalPrice + " &7credits.");
            } else {
                MessageManager.sendMessage(player, "&cUsage: /deposit <all/ingots/blocks> [amount]");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("nugget") || args[0].equalsIgnoreCase("nuggets") || args[0].equalsIgnoreCase("n")) {
                DepositObject dp = sellNuggets(args[1], player);
                if (dp == null)
                    return;

                int price = dp.getPrice();
                int amount = dp.getAmount();

                StaticClasses.economyManager.deposit(player.getUniqueId(), price);
                handleEvent(player.getUniqueId(), price);
                PlayerUtility.removeItems(player.getInventory(), new ItemStack(Material.GOLD_NUGGET), amount);
                player.updateInventory();
                MessageManager.sendMessage(player, "&7You have deposited &c" + amount + " &7gold ingots in your account for &c\u00A9" + price + " &7credits.");
            } else if (args[0].equalsIgnoreCase("ingot") || args[0].equalsIgnoreCase("ingots") || args[0].equalsIgnoreCase("i")) {
                DepositObject dp = sellIngots(args[1], player);
                if (dp == null)
                    return;

                int amount = dp.getAmount();
                int price = dp.getPrice();

                StaticClasses.economyManager.deposit(player.getUniqueId(), price);
                handleEvent(player.getUniqueId(), price);
                PlayerUtility.removeItems(player.getInventory(), new ItemStack(Material.GOLD_INGOT), amount);
                player.updateInventory();
                MessageManager.sendMessage(player, "&7You have deposited &c" + amount + " &7gold ingots in your account for &c\u00A9" + price + " &7credits.");
            } else if (args[0].equalsIgnoreCase("block") || args[0].equalsIgnoreCase("blocks") || args[0].equalsIgnoreCase("b")) {
                DepositObject dp = sellBlocks(args[1], player);
                if (dp == null)
                    return;

                int amount = dp.getAmount();
                int price = dp.getPrice();

                StaticClasses.economyManager.deposit(player.getUniqueId(), price);
                handleEvent(player.getUniqueId(), price);
                PlayerUtility.removeItems(player.getInventory(), new ItemStack(Material.GOLD_BLOCK), amount);
                MessageManager.sendMessage(player, "&7You have deposited &c" + amount + " &7gold ingots in your account for &c\u00A9" + price + " &7credits.");
            } else
                MessageManager.sendMessage(player, "&cUsage: /deposit <all/ingots/blocks> [amount]");
        } else
            MessageManager.sendMessage(player, "&cUsage: /deposit <all/ingots/blocks> [amount]");
    }

    private void handleEvent(UUID uuid, int amount) {
        List<Task> tasks = StaticClasses.questHandler.getTasks(uuid);
        for (Task task : tasks) {
            if (task instanceof DepositMoneyTask) {
                task.incrementProgress(uuid, amount);
            }
        }
    }

    private DepositObject sellBlocks(String argument, Player player) {
        int totalPrice;
        int amount;
        int itemAmount = 0;

        boolean attemptDupe = false;
        ItemStack itemStack = null;

        for (ItemStack item : player.getInventory().all(Material.GOLD_BLOCK).values())
            if (!item.hasItemMeta() && !(item.getItemMeta().hasDisplayName() || item.getItemMeta().hasLore())) {
                itemAmount += item.getAmount();
            } else {
                attemptDupe = true;
                itemStack = item;
            }

        if (attemptDupe) {
            MessageManager.sendMessage(player, "&7You cannot deposit re-named gold ingots.");

            for (Player all : PlayerUtility.getOnlinePlayers())
                if (all.hasPermission("carbyne.notify")) {
                    MessageManager.sendMessage(all, "&c[&4Dupe Attempt&c]: " + player.getName() + " attempted to dupe gold.");
                    MessageManager.sendMessage(all, "&cDisplayName: " + itemStack.getItemMeta().getDisplayName());
                }
        }

        if (itemAmount <= 0) {
            return new DepositObject(0, 0);
        }

        if (argument.equalsIgnoreCase("all"))
            amount = itemAmount;
        else {
            try {
                amount = Integer.parseInt(argument);
            } catch (Exception e) {
                MessageManager.sendMessage(player, "&7You must enter a valid number.");
                return null;
            }

            if (amount < 0) {
                MessageManager.sendMessage(player, "&7You cannot deposit negative numbers.");
                return null;
            }
        }

        if (amount == 0) {
            MessageManager.sendMessage(player, "&7Please deposit a positive amount of gold blocks.");
            return null;
        }

        if (amount > itemAmount) {
            MessageManager.sendMessage(player, "&7You do not have that many gold blocks in your inventory.");
            return null;
        }

        totalPrice = amount * 81 * StaticClasses.economyManager.getGoldWorth();
        return new DepositObject(totalPrice, amount);
    }

    private DepositObject sellIngots(String argument, Player player) {
        int price, amount, itemAmount = 0;

        boolean attemptDupe = false;
        ItemStack itemStack = null;

        for (ItemStack item : player.getInventory().all(Material.GOLD_INGOT).values())
            if (!item.hasItemMeta() && !(item.getItemMeta().hasDisplayName() || item.getItemMeta().hasLore()))
                itemAmount += item.getAmount();
            else {
                attemptDupe = true;
                itemStack = item;
            }

        if (attemptDupe) {
            MessageManager.sendMessage(player, "&7You cannot deposit re-named gold ingots.");

            for (Player all : PlayerUtility.getOnlinePlayers())
                if (all.hasPermission("carbyne.notify")) {
                    MessageManager.sendMessage(all, "&c[&4Dupe Attempt&c]: " + player.getName() + " attempted to dupe gold.");
                    MessageManager.sendMessage(all, "&cDisplayName: " + itemStack.getItemMeta().getDisplayName());
                }
        }

        if (itemAmount <= 0) {
            return new DepositObject(0, 0);
        }

        if (argument.equalsIgnoreCase("all"))
            amount = itemAmount;
        else {
            try {
                amount = Integer.parseInt(argument);
            } catch (Exception e) {
                MessageManager.sendMessage(player, "&7You must enter a valid number.");
                return null;
            }

            if (amount < 0) {
                MessageManager.sendMessage(player, "&7You cannot deposit negative numbers.");
                return null;
            }
        }

        if (amount == 0) {
            MessageManager.sendMessage(player, "&7Please deposit a positive amount of gold ingots.");
            return null;
        }

        if (amount > itemAmount) {
            MessageManager.sendMessage(player, "&7You do not have that many gold ingots in your inventory.");
            return null;
        }

        price = amount * 9 * StaticClasses.economyManager.getGoldWorth();
        return new DepositObject(price, amount);
    }

    private DepositObject sellNuggets(String argument, Player player) {
        int price, amount, itemAmount = 0;

        boolean attemptDupe = false;
        ItemStack itemStack = null;

        for (ItemStack item : player.getInventory().all(Material.GOLD_NUGGET).values())
            if (!item.hasItemMeta() && !(item.getItemMeta().hasDisplayName() || item.getItemMeta().hasLore()))
                itemAmount += item.getAmount();
            else {
                attemptDupe = true;
                itemStack = item;
            }

        if (attemptDupe) {
            MessageManager.sendMessage(player, "&7You cannot deposit re-named gold ingots.");

            for (Player all : PlayerUtility.getOnlinePlayers())
                if (all.hasPermission("carbyne.notify")) {
                    MessageManager.sendMessage(all, "&c[&4Dupe Attempt&c]: " + player.getName() + " attempted to dupe gold.");
                    MessageManager.sendMessage(all, "&cDisplayName: " + itemStack.getItemMeta().getDisplayName());
                }
        }

        if (itemAmount <= 0)
            return new DepositObject(0, 0);

        if (argument.equalsIgnoreCase("all"))
            amount = itemAmount;
        else {
            try {
                amount = Integer.parseInt(argument);
            } catch (Exception e) {
                MessageManager.sendMessage(player, "&7You must enter a valid number.");
                return null;
            }

            if (amount < 0) {
                MessageManager.sendMessage(player, "&7You cannot deposit negative numbers.");
                return null;
            }
        }

        if (amount == 0) {
            MessageManager.sendMessage(player, "&7Please deposit a positive amount of gold ingots.");
            return null;
        }

        if (amount > itemAmount) {
            MessageManager.sendMessage(player, "&7You do not have that many gold ingots in your inventory.");
            return null;
        }

        price = amount * StaticClasses.economyManager.getGoldWorth();
        return new DepositObject(price, amount);
    }

    private class DepositObject {
        @Getter
        private int price, amount;

        public DepositObject(int price, int amount) {
            this.price = price;
            this.amount = amount;
        }
    }
}