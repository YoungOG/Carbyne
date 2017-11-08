package com.medievallords.carbyne.gear.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class GearCommands extends BaseCommand {

    @Command(name = "carbyne", aliases = {"cg"}, inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            MessageManager.sendMessage(sender, "&cUsage: /carbyne store");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("store")) {
                ((Player) sender).openInventory(getGearManager().getGearGuiManager().getStoreGui());
            } else if (args[0].equalsIgnoreCase("polish")) {
                Player player = (Player) sender;
                ItemStack itemStackInHand = player.getItemInHand();

                if (itemStackInHand == null || itemStackInHand.getType() == Material.AIR) {
                    MessageManager.sendMessage(player, "&cYou must be holding Carbyne Armor to polish.");
                    return;
                }

                if (!getGearManager().isCarbyneArmor(itemStackInHand)) {
                    MessageManager.sendMessage(player, "&cYou must be holding Carbyne Armor to polish.");
                    return;
                }

                if (!player.getInventory().containsAtLeast(getGearManager().getPolishItem(), 1)) {
                    MessageManager.sendMessage(player, "&cYou need at least 1 polishing cloth to polish this.");
                    return;
                }

                PlayerUtility.removeItems(player.getInventory(), getGearManager().getPolishItem(), 1);

                CarbyneArmor armor = getGearManager().getCarbyneArmor(itemStackInHand);
                player.setItemInHand(armor.getPolishedItem());
                MessageManager.sendMessage(player, "&aYou have successfully polished your &b" + armor.getDisplayName() + " &aCarbyne piece!");
            } else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("carbyne.administrator")) {
                getGearManager().getCarbyneGear().clear();
                getGearManager().getDefaultArmors().clear();
                getGearManager().getDefaultWeapons().clear();

                getGearManager().load(YamlConfiguration.loadConfiguration(new File(getCarbyne().getDataFolder(), "gear.yml")));
                getGearManager().loadTokenOptions(getCarbyne().getGearFileConfiguration());
                getGearManager().loadPolishOptions(getCarbyne().getGearFileConfiguration());

                getCarbyne().getGearManager().getGearGuiManager().reloadStoreGuis();

                MessageManager.sendMessage(sender, "&aSuccessfully reloaded all Carbyne configurations.");
            } else {
                MessageManager.sendMessage(sender, "&cUsage: /carbyne store");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("money") && sender.hasPermission("carbyne.administrator")) {
                try {
                    int amount = Integer.parseInt(args[1]);

                    ItemStack tokenItem = getGearManager().getTokenItem();
                    tokenItem.setAmount(amount);
                    ((Player) sender).getInventory().addItem(tokenItem);
                    MessageManager.sendMessage(sender, "&aSuccessfully received &c" + amount + " &aof &b" + ChatColor.stripColor(Carbyne.getInstance().getGearManager().getTokenItem().getItemMeta().getDisplayName()) + "&a.");
                } catch (NumberFormatException e) {
                    MessageManager.sendMessage(sender, "&cPlease enter a valid amount.");
                }
            }

            if (args[0].equalsIgnoreCase("polish") && sender.hasPermission("carbyne.administrator")) {
                try {
                    int amount = Integer.parseInt(args[1]);

                    ItemStack polishItem = getGearManager().getPolishItem();
                    polishItem.setAmount(amount);
                    ((Player) sender).getInventory().addItem(polishItem);
                    MessageManager.sendMessage(sender, "&aSuccessfully received &c" + amount + " &aof &b" + ChatColor.stripColor(Carbyne.getInstance().getGearManager().getPolishItem().getItemMeta().getDisplayName()) + "&a.");
                } catch (NumberFormatException e) {
                    MessageManager.sendMessage(sender, "&cPlease enter a valid amount.");
                }
            }
        } else {
            MessageManager.sendMessage(sender, "&cUsage: /carbyne store");
        }
    }
}
