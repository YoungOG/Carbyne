package com.medievallords.carbyne.gear.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.region.Region;
import com.medievallords.carbyne.region.RegionUser;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class GearCommands extends BaseCommand {

    @Command(name = "carbyne", aliases = {"cg"}, inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            MessageManager.sendMessage(sender, "&cUsage: &7/carbyne store");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("store")) {
                MessageManager.sendMessage(sender, "&cThe carbyne store has been replaced with the carbyne forge.");
            } else if (args[0].equalsIgnoreCase("polish")) {
                Player player = (Player) sender;
                ItemStack itemStackInHand = player.getInventory().getItemInMainHand();

                if (itemStackInHand == null || itemStackInHand.getType() == Material.AIR) {
                    MessageManager.sendMessage(player, "&cYou must be holding Carbyne Armor to polish.");
                    return;
                }

                if (!StaticClasses.gearManager.isCarbyneArmor(itemStackInHand)) {
                    MessageManager.sendMessage(player, "&cYou must be holding Carbyne Armor to polish.");
                    return;
                }

                if (!player.getInventory().containsAtLeast(StaticClasses.gearManager.getPolishItem(), 1)) {
                    MessageManager.sendMessage(player, "&cYou need at least 1 polishing cloth to polish this.");
                    return;
                }

                PlayerUtility.removeItems(player.getInventory(), StaticClasses.gearManager.getPolishItem(), 1);

                CarbyneArmor armor = StaticClasses.gearManager.getCarbyneArmor(itemStackInHand);
                player.setItemInHand(armor.getPolishedItem());
                MessageManager.sendMessage(player, "&aYou have successfully polished your &b" + armor.getDisplayName() + " &aCarbyne piece!");
            } else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("carbyne.administrator")) {
                StaticClasses.gearManager.getCarbyneGear().clear();
                StaticClasses.gearManager.getDefaultArmors().clear();
                StaticClasses.gearManager.getDefaultWeapons().clear();

                try {
                    Carbyne.getInstance().setGearFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getGearFile()));
                    Carbyne.getInstance().getGearFileConfiguration().save(Carbyne.getInstance().getGearFile());
                } catch (IOException e) {
                    e.printStackTrace();
                    MessageManager.sendMessage(sender, "&cError, check console");
                    return;
                }

                StaticClasses.gearManager.load(Carbyne.getInstance().getGearFileConfiguration());

                //StaticClasses.gearManager.getGearGuiManager().reloadStoreGuis();

                MessageManager.sendMessage(sender, "&aSuccessfully reloaded all Carbyne configurations.");
            } else
                MessageManager.sendMessage(sender, "&cUsage: /carbyne store");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("money") && sender.hasPermission("carbyne.administrator")) {
                try {
                    int amount = Integer.parseInt(args[1]);

                    ItemStack tokenItem = StaticClasses.gearManager.getTokenItem();
                    tokenItem.setAmount(amount);
                    ((Player) sender).getInventory().addItem(tokenItem);
                    MessageManager.sendMessage(sender, "&aSuccessfully received &c" + amount + " &aof &b" + ChatColor.stripColor(StaticClasses.gearManager.getTokenItem().getItemMeta().getDisplayName()) + "&a.");
                } catch (NumberFormatException e) {
                    MessageManager.sendMessage(sender, "&cPlease enter a valid amount.");
                }
            } else if (args[0].equalsIgnoreCase("polish") && sender.hasPermission("carbyne.administrator")) {
                try {
                    int amount = Integer.parseInt(args[1]);

                    ItemStack polishItem = StaticClasses.gearManager.getPolishItem();
                    polishItem.setAmount(amount);
                    ((Player) sender).getInventory().addItem(polishItem);
                    MessageManager.sendMessage(sender, "&aSuccessfully received &c" + amount + " &aof &b" + ChatColor.stripColor(StaticClasses.gearManager.getPolishItem().getItemMeta().getDisplayName()) + "&a.");
                } catch (NumberFormatException e) {
                    MessageManager.sendMessage(sender, "&cPlease enter a valid amount.");
                }
            } else if ((args[0].equalsIgnoreCase("prizeegg") || args[0].equalsIgnoreCase("pb")) && sender.hasPermission("carbyne.administrator")) {
                try {
                    int amount = Integer.parseInt(args[1]);

                    ItemStack prizeEggItem = StaticClasses.gearManager.getPrizeEggItem();
                    prizeEggItem.setAmount(amount);
                    ((Player) sender).getInventory().addItem(prizeEggItem);
                    MessageManager.sendMessage(sender, "&aSuccessfully received &c" + amount + " &aof &b" + ChatColor.stripColor(StaticClasses.gearManager.getPrizeEggItem().getItemMeta().getDisplayName()) + "&a.");
                } catch (NumberFormatException e) {
                    MessageManager.sendMessage(sender, "&cPlease enter a valid amount.");
                }
            } else if (args[0].equalsIgnoreCase("addRegion") && sender.hasPermission("carbyne.administrator")) {
                Player player = (Player) sender;
                RegionUser user = RegionUser.getRegionUser(player);

                Region region = new Region(args[1]);

                if (user.getSelection() == null)
                    MessageManager.sendMessage(player, "&cYou must select 2 points first.");
                else if (user.getSelection().getLocation1() == null || user.getSelection().getLocation2() == null)
                    MessageManager.sendMessage(player, "&cYou must select 2 points first.");
                else if (user.getSelection().getLocation1().getWorld() != user.getSelection().getLocation2().getWorld())
                    MessageManager.sendMessage(player, "&cYour selection must be in the same world.");
                else {
                    MessageManager.sendMessage(player, "&aSuccessfully added the region &b" + region.getName() + " &ato the nerf list.");
                    region.setSelection(user.getSelection());
                }
            } else if (args[0].equalsIgnoreCase("delRegion") && sender.hasPermission("carbyne.administrator")) {
                Player player = (Player) sender;
                Region region = null;

                for (Region r : StaticClasses.gearManager.getNerfedRegions()) {
                    if (r.getName().equalsIgnoreCase(args[1]))
                        region = r;
                }

                if (region == null) {
                    MessageManager.sendMessage(player, "&cCould not find the region specified.");
                    return;
                }

                StaticClasses.gearManager.getNerfedRegions().remove(region);
                ConfigurationSection cs = Carbyne.getInstance().getGearFileConfiguration();

                if (cs.contains("NerfedCarbyneRegions." + region.getName()))
                    cs.set("NerfedCarbyneRegions." + region.getName(), null);

                try {
                    Carbyne.getInstance().getGearFileConfiguration().save(Carbyne.getInstance().getGearFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MessageManager.sendMessage(player, "&cSuccessfully removed the region &b" + region.getName() + " &cfrom the nerf list.");
            } else
                MessageManager.sendMessage(sender, "&cUsage: &7/carbyne store");
        } else
            MessageManager.sendMessage(sender, "&cUsage: &7/carbyne store");
    }
}
