package com.medievallords.carbyne.squads.commands;

import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by xwiena22 on 2017-03-13
 * for the Carbyne project.
 */
public class SquadDisbandCommand extends BaseCommand {

    @Command(name = "squad.disband", inGameOnly = true, aliases = {"d"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Squad squad = getSquadManager().getSquad(player.getUniqueId());

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /squad");
            return;
        }

        if (squad == null) {
            MessageManager.sendMessage(player, "&cYou are not in a squad.");
            return;
        }

        squad.disbandParty(player.getUniqueId());
    }
}
