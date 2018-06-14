package com.medievallords.carbyne.gear.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.region.Region;
import com.medievallords.carbyne.region.RegionUser;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class GearCommands extends BaseCommand {

    @Command(name = "carbyne", aliases = {"cg"}, inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            MessageManager.sendMessage(sender, "&cUsage: /carbyne store");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("store")) {
                Player player = (Player) sender;
                player.openInventory(getGearManager().getGearGuiManager().getStoreGui());
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, .8f);
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
            } else if (args[0].equalsIgnoreCase("polish") && sender.hasPermission("carbyne.administrator")) {
                try {
                    int amount = Integer.parseInt(args[1]);

                    ItemStack polishItem = getGearManager().getPolishItem();
                    polishItem.setAmount(amount);
                    ((Player) sender).getInventory().addItem(polishItem);
                    MessageManager.sendMessage(sender, "&aSuccessfully received &c" + amount + " &aof &b" + ChatColor.stripColor(Carbyne.getInstance().getGearManager().getPolishItem().getItemMeta().getDisplayName()) + "&a.");
                } catch (NumberFormatException e) {
                    MessageManager.sendMessage(sender, "&cPlease enter a valid amount.");
                }
            } else if (args[0].equalsIgnoreCase("addRegion") && sender.hasPermission("carbyne.administrator")) {
                Player player = (Player) sender;
                RegionUser user = RegionUser.getRegionUser(player);

                Region region = new Region(args[1]);

                if (user.getSelection() == null) {
                    MessageManager.sendMessage(player, "&cYou must select 2 points first.");
                } else if (user.getSelection().getLocation1() == null || user.getSelection().getLocation2() == null) {
                    MessageManager.sendMessage(player, "&cYou must select 2 points first.");
                } else if (user.getSelection().getLocation1().getWorld() != user.getSelection().getLocation2().getWorld()) {
                    MessageManager.sendMessage(player, "&cYour selection must be in the same world.");
                } else {
                    MessageManager.sendMessage(player, "&aSuccessfully added the region &b" + region.getName() + " &ato the nerf list.");
                    region.setSelection(user.getSelection());
                }
            } else if (args[0].equalsIgnoreCase("delRegion") && sender.hasPermission("carbyne.administrator")) {
                Player player = (Player) sender;
                Region region = null;

                for (Region r : getGearManager().getNerfedRegions()) {
                    if (r.getName().equalsIgnoreCase(args[1]))
                        region = r;
                }

                if (region == null) {
                    MessageManager.sendMessage(player, "&cCould not find the region specified.");
                    return;
                }

                getGearManager().getNerfedRegions().remove(region);
                ConfigurationSection cs = getCarbyne().getGearFileConfiguration();

                if (cs.contains("NerfedCarbyneRegions." + region.getName()))
                    cs.set("NerfedCarbyneRegions." + region.getName(), null);

                try {
                    getCarbyne().getGearFileConfiguration().save(getCarbyne().getGearFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MessageManager.sendMessage(player, "&cSuccessfully removed the region &b" + region.getName() + " &cfrom the nerf list.");
            } else {
                MessageManager.sendMessage(sender, "&cUsage: /carbyne store");
            }
        } else {
            MessageManager.sendMessage(sender, "&cUsage: /carbyne store");
        }
    }
}
