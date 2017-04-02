package com.medievallords.carbyne.crates.commands;

import com.medievallords.carbyne.crates.Crate;
import com.medievallords.carbyne.crates.keys.Key;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.Lang;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrateListCommand extends BaseCommand {

    @Command(name = "crate.list", permission = "utils.commands.crate.list")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 1) {
            MessageManager.sendMessage(sender, Lang.TOO_FEW_ARGS_CRATE_LIST.toString());
            return;
        }

        if (args.length > 1) {
            MessageManager.sendMessage(sender, Lang.TOO_MANY_ARGS_CRATE_LIST.toString());
            return;
        }

        if (args[0].equalsIgnoreCase("crates")) {
            if (getCrateManager().getCrates().size() <= 0) {
                MessageManager.sendMessage(sender, Lang.CRATE_LIST_NO_CRATES.toString());
                return;
            }

            if (sender instanceof Player) {
                Player player = (Player) sender;

                MessageManager.sendMessage(sender,  Lang.SUCCESS_CRATE_LIST_HEADER.toString().replace("{TYPE}", "Crates"));

                JSONMessage message = JSONMessage.create();
                for (int i = 0; i < getCrateManager().getCrates().size(); i++) {
                    if (i < getCrateManager().getCrates().size() - 1) {
                        Crate crate = getCrateManager().getCrates().get(i);

                        message.then(crate.getName()).color(ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_NAME_COLOR.toString()))
                                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aLocation: " + (crate.getLocation() != null ? "World: &b" + crate.getLocation().getWorld().getName() + "&a, X: &b" + crate.getLocation().getBlockX() + "&a, Y: &b" + crate.getLocation().getBlockY() + "&a, Z: &b" + crate.getLocation().getBlockZ() + "&a)" : "&cNot set") + "\n&aRewards Amount: &b" + crate.getRewardsAmount()))
                                .then(", ").color(ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_COMMA_COLOR.toString()));
                    } else {
                        Crate crate = getCrateManager().getCrates().get(i);

                        message.then(crate.getName()).color(ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_NAME_COLOR.toString()))
                                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aLocation: " + (crate.getLocation() != null ? "World: &b" + crate.getLocation().getWorld().getName() + "&a, X: &b" + crate.getLocation().getBlockX() + "&a, Y: &b" + crate.getLocation().getBlockY() + "&a, Z: &b" + crate.getLocation().getBlockZ() + "&a)" : "&cNot set") + "\n&aRewards Amount: &b" + crate.getRewardsAmount()));
                    }
                }

                message.send(player);
            } else {
                MessageManager.sendMessage(sender,  Lang.SUCCESS_CRATE_LIST_HEADER.toString().replace("{TYPE}", "Crates"));

                List<String> crateNames = new ArrayList<>();
                for (Crate crate : getCrateManager().getCrates()) {
                    crateNames.add(ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_NAME_COLOR.toString()) + crate.getName());
                }

                MessageManager.sendMessage(sender, crateNames.toString().replace("[", "").replace("]", "").replace(",", ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_COMMA_COLOR.toString()) + ","));
            }
        } else if (args[0].equalsIgnoreCase("keys")) {
            if (getCrateManager().getKeys().size() <= 0) {
                MessageManager.sendMessage(sender, Lang.CRATE_LIST_NO_KEYS.toString());
                return;
            }

            if (sender instanceof Player) {
                Player player = (Player) sender;

                MessageManager.sendMessage(sender, Lang.SUCCESS_CRATE_LIST_HEADER.toString().replace("{TYPE}", "Keys"));

                JSONMessage message = JSONMessage.create();
                for (int i = 0; i < getCrateManager().getKeys().size(); i++) {
                    if (i < getCrateManager().getKeys().size() - 1) {
                        Key key = getCrateManager().getKeys().get(i);

                        message.then(key.getName()).color(ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_NAME_COLOR.toString()))
                                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aItemID: &b" + key.getItemId() + "\n&aItemData: &b" + key.getItemData() + "\n&aDisplay Name: " + key.getDisplayName() + "\n&aCrate: " + (!key.getCrate().isEmpty() && key.getCrate() != null ? "&b" + key.getCrate() : "&cNone")))
                                .then(", ").color(ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_COMMA_COLOR.toString()));
                    } else {
                        Key key = getCrateManager().getKeys().get(i);

                        message.then(key.getName()).color(ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_NAME_COLOR.toString()))
                                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aItemID: &b" + key.getItemId() + "\n&aItemData: &b" + key.getItemData() + "\n&aDisplay Name: " + key.getDisplayName() + "\n&aCrate: " + (!key.getCrate().isEmpty() && key.getCrate() != null ? "&b" + key.getCrate() : "&cNone")));
                    }
                }

                message.send(player);
            } else {
                MessageManager.sendMessage(sender, Lang.SUCCESS_CRATE_LIST_HEADER.toString().replace("{TYPE}", "Keys"));

                List<String> keyNames = new ArrayList<>();
                for (Key key : getCrateManager().getKeys()) {
                    keyNames.add(ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_NAME_COLOR.toString()) + key.getName());
                }

                MessageManager.sendMessage(sender, keyNames.toString().replace("[", "").replace("]", "").replace(",", ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_COMMA_COLOR.toString()) + ","));
            }
        } else {
            MessageManager.sendMessage(sender, Lang.USAGE_CRATE_LIST.toString());
        }
    }
}
