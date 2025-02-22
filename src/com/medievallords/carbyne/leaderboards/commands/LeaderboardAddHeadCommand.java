package com.medievallords.carbyne.leaderboards.commands;

import com.medievallords.carbyne.leaderboards.Leaderboard;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 1/23/2017
 * for the Carbyne-Gear project.
 */
public class LeaderboardAddHeadCommand extends BaseCommand {

    @Command(name = "leaderboard.addhead", permission = "carbyne.command.leaderboard", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();
        Player player = command.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /leaderboard");
            return;
        }

        Leaderboard leaderboard = StaticClasses.leaderboardManager.getLeaderboard(args[0]);

        if (leaderboard == null) {
            MessageManager.sendMessage(player, "&cCould not find the requested leaderboard.");
            return;
        }

        Block block = player.getTargetBlock(null, 10);

        if (block == null) {
            MessageManager.sendMessage(player, "&cCould not find a block within 10 blocks of where your looking at.");
            return;
        }

        BlockState blockState = block.getState();

        if (blockState instanceof Skull) {
            Skull skull = (Skull) blockState;

            if (!skull.getSkullType().equals(SkullType.PLAYER)) {
                MessageManager.sendMessage(player, "&cYou must be looking at a player head.");
                return;
            }
        } else {
            MessageManager.sendMessage(player, "&cYou must be looking at a player head.");
            return;
        }

        if (leaderboard.getHeadLocations().contains(block.getLocation())) {
            MessageManager.sendMessage(player, "&cThis leaderboard already contains this head.");
            return;
        }

        leaderboard.getHeadLocations().add(block.getLocation());
        leaderboard.save();

        MessageManager.sendMessage(player, "&aSuccessfully added a head to the leaderboard.");
    }
}
