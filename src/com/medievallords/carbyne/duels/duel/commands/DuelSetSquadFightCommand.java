package com.medievallords.carbyne.duels.duel.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.duel.request.DuelRequest;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Williams on 2017-04-03.
 * for the Carbyne project.
 */
public class DuelSetSquadFightCommand extends BaseCommand {

    @Command(name = "duel.squadfight", aliases = {"duel.teamfight", "duel.squad", "duel.team"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        DuelRequest request = DuelRequest.getRequest(player.getUniqueId());
        if (request == null) {
            MessageManager.sendMessage(player, "&cYou can only do this command in the duel");
            return;
        }

        if (Carbyne.getInstance().getSquadManager().getSquad(player.getUniqueId()) != null) {
            Squad squad = Carbyne.getInstance().getSquadManager().getSquad(player.getUniqueId());

            for (UUID uuid : squad.getAllPlayers()) {
                Player mate = Bukkit.getServer().getPlayer(uuid);
                if (!mate.getWorld().equals(player.getWorld()) || mate.getLocation().distance(player.getLocation()) > 10) {
                    MessageManager.sendMessage(player, "&cYour squad needs to be in the duel area to start a squad fight");
                    return;
                }
            }
        }

        request.getPlayersSquadFight().put(player.getUniqueId(), true);
        request.requestSquadFight();

        if (!request.isSquadFight()) {
            for (UUID uuid : request.getPlayers().keySet()) {
                Player toSend = Bukkit.getServer().getPlayer(uuid);
                if (toSend != null) {
                    MessageManager.sendMessage(toSend, "&b," + player.getName() + " &7wants to start a squad fight, &b/duel squadfight &7| to accept");
                }
            }
        }

        request.cancelTask();
        request.runTask();
    }
}
