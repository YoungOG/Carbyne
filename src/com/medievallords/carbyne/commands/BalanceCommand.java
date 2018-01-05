package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BalanceCommand extends BaseCommand {

    @Command(name = "balance", aliases = {"bal", "money", "cash"})
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (getEconomyManager().isEconomyHalted()) {
            MessageManager.sendMessage(player, "&cThe economy is temporarily disabled. The administrators will let you know when it is re-enabled.");
            return;
        }

        if (args.length == 0) {
            Account account = Account.getAccount(player.getName());

            if (account == null) {
                MessageManager.sendMessage(player, "&cThere was an error grabbing your balance. Please try again later.");
                return;
            }

            MessageManager.sendMessage(player, "&7Balance: &c\u00A9" + MessageManager.format(account.getBalance()) + "");
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("top")) {
                if (!player.hasPermission("carbyne.commands.baltop")) {
                    MessageManager.sendMessage(player, "&cUsage: /balance");
                    return;
                }

                new BukkitRunnable() {
                    public void run() {
                        HashMap<String, Double> map = new HashMap<>();
                        for (Account account : Account.getAccounts()) {
                            map.put(account.getAccountHolder(), account.getBalance());
                        }

                        MessageManager.sendMessage(player, "&7***&cTop 10 Accounts&7***");

                        Object[] a = map.entrySet().toArray();
                        Arrays.sort(a, (o1, o2) -> ((Map.Entry<String, Double>) o2).getValue().compareTo(((Map.Entry<String, Double>) o1).getValue()));

                        int topten = 0;
                        for (Object e : a) {
                            if (topten <= 9) {
                                MessageManager.sendMessage(player, "&7" + (topten + 1) + ". &c" + ((Map.Entry<String, Double>) e).getKey() + " &7- &c\u00A9" + MessageManager.format(((Map.Entry<String, Double>) e).getValue()));
                            }

                            topten++;
                        }
                    }
                }.runTaskAsynchronously(getCarbyne());
            } else {
                MessageManager.sendMessage(player, "&cUsage: /balance");
            }
        }
    }
}
