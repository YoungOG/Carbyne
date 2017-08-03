package com.medievallords.carbyne.duels.duel.commands;

import com.medievallords.carbyne.duels.duel.request.DuelRequest;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Williams on 2017-04-03.
 * for the Carbyne project.
 */
public class DuelAcceptCommand extends BaseCommand {

    @Command(name = "duel.accept", inGameOnly = true)
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

        request.getArena().stopCancel();

        if (request.getBets().size() < 2) {
            MessageManager.sendMessage(player, "&cYou both must enter a bet");
            return;
        }

        request.getPlayers().put(player.getUniqueId(), true);
        request.sendMessageToAll("&b" + player.getName() + "&a has accepted the duel");
        request.cancelTask();

        int i = 0;
        for (UUID uuid : request.getPlayers().keySet()) {
            if (request.getPlayers().get(uuid)) {
                i++;
            }
        }

        if (i >= request.getPlayers().size()) {
            request.request();
            return;
        }

        request.runTask();

    }
}
