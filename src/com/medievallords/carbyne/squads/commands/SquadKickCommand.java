package com.medievallords.carbyne.squads.commands;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-13.
 * for the Carbyne project.
 */
public class SquadKickCommand extends BaseCommand {

    @Command(name = "squad.kick", inGameOnly = true, aliases = {"squad.k"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Squad squad = getSquadManager().getSquad(player.getUniqueId());

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /squad");
            return;
        }

        if (squad == null) {
            MessageManager.sendMessage(player, "&cYou are not in a squad.");
            return;
        }

        if (!squad.getLeader().equals(player.getUniqueId())) {
            MessageManager.sendMessage(player, "&cOnly the leader can kick players.");
            return;
        }

        Player target = Bukkit.getServer().getPlayer(args[0]);

        if (target == null) {
            MessageManager.sendMessage(player, "&cCould not find that player.");
            return;
        }

        if (target.equals(player)) {
            MessageManager.sendMessage(player, "&cYou cannot kick yourself. Use /squad leave.");
            return;
        }

        if (getSquadManager().getSquad(target.getUniqueId()) == null) {
            MessageManager.sendMessage(player, "&cThat player is not in your squad.");
            return;
        }

        if (!getSquadManager().getSquad(target.getUniqueId()).getUniqueId().equals(squad.getUniqueId())) {
            MessageManager.sendMessage(player, "&cThat player is not in your squad.");
            return;
        }

        squad.getMembers().remove(target.getUniqueId());

        MessageManager.sendMessage(target, "&cYou have been kicked from the squad.");

        squad.sendAllMembersMessage("&b" + target.getName() + " &chas been kicked from the squad.");

        Board board = Board.getByPlayer(Bukkit.getPlayer(target.getUniqueId()));

        if (board != null) {
            BoardCooldown targetCooldown = board.getCooldown("target");

            if (targetCooldown != null) {
                targetCooldown.cancel();
            }
        }
    }
}
