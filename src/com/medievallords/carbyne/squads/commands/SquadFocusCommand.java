package com.medievallords.carbyne.squads.commands;

import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import com.medievallords.carbyne.utils.scoreboard.BoardFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Calvin on 6/4/2017
 * for the Carbyne project.
 */
public class SquadFocusCommand extends BaseCommand {

    @Command(name = "focus", inGameOnly = true, aliases = {"target", "f"})
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Squad squad = getSquadManager().getSquad(player.getUniqueId());

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /focus <name>");
            return;
        }

        if (squad == null) {
            MessageManager.sendMessage(player, "&cYou are not in a squad.");
            return;
        }

        if (!squad.getLeader().equals(player.getUniqueId())) {
            MessageManager.sendMessage(player, "&cYou must be a squad leader to be able to focus.");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            MessageManager.sendMessage(player, "&cThat player could not be found.");
            return;
        }

        if (getSquadManager().getSquad(target.getUniqueId()) != null) {
            Board board = Board.getByPlayer(player);

            if (board != null) {
                BoardCooldown targetCooldown = board.getCooldown("target");

                if (targetCooldown != null) {
                    MessageManager.sendMessage(player, "&eYou cannot target for another &6" + targetCooldown.getFormattedString(BoardFormat.SECONDS) + " &eseconds!");
                } else {
                    new BoardCooldown(board, "target", 30.0D);

                    for (UUID id : squad.getMembers()) {
                        Board memberBoard = Board.getByPlayer(Bukkit.getPlayer(id));

                        if (memberBoard != null) {
                            BoardCooldown memberTargetCooldown = memberBoard.getCooldown("target");

                            if (memberTargetCooldown == null) {
                                new BoardCooldown(memberBoard, "target", 30.0D);
                            }
                        }
                    }

                    squad.setTargetSquad(getSquadManager().getSquad(target.getUniqueId()));
                    squad.setTargetUUID(null);

                    MessageManager.sendMessage(player, "&aYou have targeted &c" + target.getName() + "&a's Squad.");
                    squad.sendMembersMessage("&5" + player.getName() + " &ahas targeted &c" + target.getName() + "&a's Squad!");

                    getSquadManager().getSquad(target.getUniqueId()).sendAllMembersMessage("&aYou are being targeted by &c" + player.getName() + "&a's Squad.");
                }
            }
        } else {
            Board board = Board.getByPlayer(player);

            if (board != null) {
                BoardCooldown targetCooldown = board.getCooldown("target");

                if (targetCooldown != null) {
                    MessageManager.sendMessage(player, "&eYou cannot target for another &6" + targetCooldown.getFormattedString(BoardFormat.SECONDS) + " &eseconds!");
                } else {
                    new BoardCooldown(board, "target", 30.0D);

                    for (UUID id : squad.getMembers()) {
                        Board memberBoard = Board.getByPlayer(Bukkit.getPlayer(id));

                        if (memberBoard != null) {
                            BoardCooldown memberTargetCooldown = memberBoard.getCooldown("target");

                            if (memberTargetCooldown == null) {
                                new BoardCooldown(memberBoard, "target", 30.0D);
                            }
                        }
                    }

                    squad.setTargetUUID(target.getUniqueId());
                    squad.setTargetSquad(null);

                    MessageManager.sendMessage(player, "&aYou have targeted &c" + target.getName() + "&a.");
                    squad.sendMembersMessage("&5" + player.getName() + " &ahas targeted &c" + target.getName() + "&a!");

                    MessageManager.sendMessage(target, "&aYou are being targeted by &c" + player.getName() + "&a!");
                }
            }
        }
    }
}
