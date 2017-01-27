package com.medievallords.carbyne.gear.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class GearCommands implements CommandExecutor{

    private Carbyne main = Carbyne.getInstance();
    private GearManager gearManager = main.getGearManager();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("carbyne") || cmd.getName().equalsIgnoreCase("cg")) {
			if (args.length == 0) {
                MessageManager.sendMessage(sender, "&cUsage: /carbyne store");
                return true;
			} else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("store")) {
                    if (!Carbyne.getInstance().getGearManager().isEnableStore()) {
                        return true;
                    }

                    ((Player) sender).openInventory(Carbyne.getInstance().getGuiManager().getStoreGui());
                } else if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("carbyne.admin")) {
                    gearManager.getCarbyneGear().clear();
                    gearManager.getDefaultArmors().clear();
                    gearManager.getDefaultWeapons().clear();

                    gearManager.load(YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "gearconfig.yml")));
                    gearManager.loadStoreOptions(YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "store.yml")));

                    main.getGuiManager().reloadStoreGuis();

                    MessageManager.sendMessage(sender, "&aSuccessfully reloaded Carbyne configurations.");
                } else {
                    MessageManager.sendMessage(sender, "&cUsage: /carbyne store");
                    return true;
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("money") && sender.hasPermission("carbyne.admin")) {
                    try {
                        int amount = Integer.parseInt(args[1]);

                        ((Player) sender).getInventory().addItem(new ItemBuilder(Carbyne.getInstance().getGearManager().getMoney()).amount(amount).build());
                        MessageManager.sendMessage(sender, "&aSuccessfully received &c" + amount + " &aof &b" + Carbyne.getInstance().getGearManager().getMoneyItem() + "&a.");
                    } catch (NumberFormatException e) {
                        MessageManager.sendMessage(sender, "&cPlease enter a valid amount.");
                        return true;
                    }
                }
            } else {
                MessageManager.sendMessage(sender, "&cUsage: /carbyne store");
                return true;
            }
		}

		return true;
	}
}
