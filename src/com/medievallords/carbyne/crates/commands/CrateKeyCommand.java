package com.medievallords.carbyne.crates.commands;

import com.medievallords.carbyne.crates.keys.Key;
import com.medievallords.carbyne.utils.Lang;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrateKeyCommand extends BaseCommand {

    @Command(name = "crate.key", permission = "utils.commands.crate.key")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 4) {
            MessageManager.sendMessage(sender, Lang.TOO_FEW_ARGS_CRATE_KEY.toString());
            return;
        }

        if (args.length > 4) {
            MessageManager.sendMessage(sender, Lang.TOO_MANY_ARGS_CRATE_KEY.toString());
            return;
        }

        if (args[0].equalsIgnoreCase("give")) {
            String who = args[1];
            String name = args[2];

            if (!isInteger(args[3])) {
                MessageManager.sendMessage(sender, Lang.CRATE_KEY_VALID_AMOUNT.toString());
                return;
            }

            int amount = Integer.valueOf(args[3]);

            if (getCrateManager().getKeys().size() <= 0) {
                MessageManager.sendMessage(sender, Lang.CRATE_KEYS_NO_KEYS.toString());
                return;
            }

            Key key = getCrateManager().getKey(name);

            if (key == null) {
                MessageManager.sendMessage(sender, Lang.CRATE_KEYS_NOT_FOUND.toString().replace("{NAME}", name));
                return;
            }

            if (who.equalsIgnoreCase("-a")) {
                for (Player all : PlayerUtility.getOnlinePlayers()) {
                    all.getInventory().addItem(key.getItem(amount));
                }

                MessageManager.sendMessage(sender, Lang.SUCCESS_CRATE_KEY.toString().replace("{NAME}", "everyone").replace("{KEY_NAME}", key.getName()).replace("{AMOUNT}", "" + amount));
            } else {
                Player player = Bukkit.getPlayer(who);

                if (player == null) {
                    MessageManager.sendMessage(sender, Lang.ERROR_PLAYER_NOT_FOUND.toString().replace("{NAME}", who));
                    return;
                }

                player.getInventory().addItem(key.getItem(amount));

                MessageManager.sendMessage(sender, Lang.SUCCESS_CRATE_KEY.toString().replace("{NAME}", player.getName()).replace("{KEY_NAME}", key.getName()).replace("{AMOUNT}", "" + amount));
            }
        } else {
            MessageManager.sendMessage(sender, Lang.USAGE_CRATE_KEY.toString());
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }

        return true;
    }
}
