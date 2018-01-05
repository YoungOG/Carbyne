package com.medievallords.carbyne.duels.duel.commands;

import com.medievallords.carbyne.duels.duel.request.DuelRequest;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Williams on 2017-04-03
 * for the Carbyne project.
 */
public class DuelBetCommand extends BaseCommand {

    @Command(name = "duel.bet", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /duel bet <amount>");
            return;
        }

        DuelRequest request = DuelRequest.getRequest(player.getUniqueId());
        if (request == null) {
            MessageManager.sendMessage(player, "&cYou can only do this command in the duel");
            return;
        }

        if (request.getBets().containsKey(player.getUniqueId())) {
            MessageManager.sendMessage(player, "&cYou have already placed a bet");
            return;
        }

        try {
            int bet = Integer.parseInt(args[0]);
            if (bet < 65) {
                MessageManager.sendMessage(player, "&cYou need to enter &b65 &cor more");
                return;
            }

            //noinspection ConstantConditions
            if (Account.getAccount(player.getUniqueId()) != null && Account.getAccount(player.getUniqueId()).getBalance() < bet) {
                MessageManager.sendMessage(player, "&cYou don't have enough money");
                return;
            }

            //noinspection ConstantConditions
            if (Account.getAccount(player.getUniqueId()) != null && Account.getAccount(player.getUniqueId()).getBalance() < 65) {
                MessageManager.sendMessage(player, "&cYou don't have enough money");
                return;
            }

            for (UUID uuid : request.getBets().keySet()) {
                if (uuid.equals(player.getUniqueId())) {
                    continue;
                }

                if (request.getBets().get(uuid) - 3 > bet) {
                    MessageManager.sendMessage(player, "&cYour opponent has bet &b " + request.getBets().get(uuid) + ". &cYou need to bet atleast " + (request.getBets().get(uuid) - 3) + " credits");
                    request.cancelTask();
                    request.runTask();
                    return;
                }

                if (request.getBets().get(uuid) < bet - 3) {
                    MessageManager.sendMessage(Bukkit.getPlayer(uuid), "&cYour opponent has bet &b " + bet + ". &cYou need to bet atleast " + (bet - 3) + "credits");
                    if (Account.getAccount(uuid) != null) {
                        Account.getAccount(uuid).setBalance(Account.getAccount(uuid).getBalance() + request.getBets().get(uuid));
                    }

                    request.getBets().remove(uuid);
                    request.cancelTask();
                    request.runTask();
                    return;
                }

                //noinspection ConstantConditions
                /*if (Account.getAccount(player.getUniqueId()) != null && (Account.getAccount(player.getUniqueId()).getBalance() - 3) < request.getBets().get(uuid)) {
                    MessageManager.sendMessage(player, "&cYou don't have enough money");
                    return;
                }*/
            }

            request.getBets().put(player.getUniqueId(), bet);
            if (Account.getAccount(player.getUniqueId()) != null)
                //noinspection ConstantConditions
                Account.getAccount(player.getUniqueId()).setBalance(Account.getAccount(player.getUniqueId()).getBalance() - bet);
            request.sendMessageToAll("&b" + player.getName() + "&a has bet &b" + bet);

        } catch (NumberFormatException e) {
            MessageManager.sendMessage(player, "&cA bet can only be a number");
        }

        if (request.getBets().size() >= 2) {
            for (UUID uuid : request.getPlayers().keySet()) {
                Player playerTo = Bukkit.getServer().getPlayer(uuid);
                if (playerTo != null) {
                    JSONMessage.create("Both bets placed. ").color(ChatColor.GREEN).suggestCommand("/duel accept").tooltip(ChatColor.LIGHT_PURPLE + "Click to use command")
                            .then("/duel accept").color(ChatColor.AQUA).suggestCommand("/duel accept").tooltip(ChatColor.LIGHT_PURPLE + "Click to use command")
                            .then(" to start the duel").color(ChatColor.GREEN).suggestCommand("/duel accept")
                            .tooltip(ChatColor.LIGHT_PURPLE + "Click to use command").send(playerTo);
                }
            }
        }

        request.cancelTask();
        request.runTask();
    }
}
