package com.medievallords.carbyne.squads.commands;

import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.squads.SquadType;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-13.
 * for the Carbyne project.
 */
public class SquadSetCommand extends BaseCommand{

    @Command(name = "squad.set", inGameOnly = true, aliases = {"s"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Squad squad = getSquadManager().getSquad(player.getUniqueId());

        if (args.length < 0 || args.length > 1) {
            MessageManager.sendMessage(player, "&cUsage: /squad");
            return;
        }

        if (args.length == 0) {
            if (squad.getType() == SquadType.PRIVATE) {
                squad.setType(SquadType.PUBLIC);
                MessageManager.sendMessage(player, "&aThe squad has been set to &bPublic&a.");
            } else {
                squad.setType(SquadType.PRIVATE);
                MessageManager.sendMessage(player, "&aThe squad has been set to &bPrivate&a.");
            }
        }

        if (args.length == 1) {
            try {
                squad.setType(SquadType.valueOf(args[0].toUpperCase()));
                MessageManager.sendMessage(player, "&aThe squad has been set to &b" + args[0].toLowerCase().substring(0, 1).toUpperCase() + args[0].toLowerCase().substring(1) + "&a.");
            } catch (Exception exception) {
                MessageManager.sendMessage(player, "&cYou can only set the squad to public or private.");
            }
        }
    }
}
