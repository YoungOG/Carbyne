package com.medievallords.carbyne.duels.duel.commands;

import com.medievallords.carbyne.duels.duel.request.DuelRequest;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-04-03
 * for the Carbyne project.
 */
public class DuelDeclineCommand extends BaseCommand {

    @Command(name = "duel.decline", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /duel");
            return;
        }

        DuelRequest request = DuelRequest.getRequest(player.getUniqueId());
        if (request == null) {
            MessageManager.sendMessage(player, "&cYou can only do this command in the duel");
            return;
        }

        request.cancel();
        request.sendMessageToAll("&b" + player.getName() + "&c has cancelled the duel");

        request.cancelTask();
    }
}
