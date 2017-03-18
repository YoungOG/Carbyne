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
public class SquadLeaveCommand extends BaseCommand {

    @Command(name = "squad.leave", inGameOnly = true, aliases = {"l"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /squad");
            return;
        }

        Squad squad = getSquadManager().getSquad(player.getUniqueId());

        if (squad == null) {
            MessageManager.sendMessage(player, "&cYou are not in a squad.");
            return;
        }

        if (squad.getLeader().equals(player.getUniqueId())) {
            if (squad.getMembers().size() > 0) {
                squad.setLeader(squad.getMembers().get(0));
                squad.getMembers().remove(squad.getMembers().get(0));

                MessageManager.sendMessage(player, "&cYou have left the squad.");
                squad.sendAllMembersMessage("&b" + player.getName() + " &chas left the squad.");
                MessageManager.sendMessage(squad.getLeader(), "&aThe previous squad leader has left. You are the now the new squad leader.");
            } else {
                squad.disbandParty(player.getUniqueId());
            }

            return;
        }

        squad.getMembers().remove(player.getUniqueId());

        MessageManager.sendMessage(player, "&cYou have left the squad.");

        squad.sendAllMembersMessage("&b" + player.getName() + " &chas left the squad.");
    }
}
