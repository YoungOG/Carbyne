package com.medievallords.carbyne.leaderboards.commands;

import com.medievallords.carbyne.leaderboards.Leaderboard;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * Created by Calvin on 1/23/2017
 * for the Carbyne-Gear project.
 */
public class LeaderboardDelSignCommand extends BaseCommand {

    @Command(name = "leaderboard.delsign", permission = "carbyne.command.leaderboard", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();
        Player player = command.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /leaderboard");
            return;
        }

        Leaderboard leaderboard = getLeaderboardManager().getLeaderboard(args[0]);

        if (leaderboard == null) {
            MessageManager.sendMessage(player, "&cCould not find the requested leaderboard.");
            return;
        }

        Block block = player.getTargetBlock((HashSet<Byte>) null, 10);

        if (block == null) {
            MessageManager.sendMessage(player, "&cCould not find a block within 10 blocks of where your looking at.");
            return;
        }

        BlockState blockState = block.getState();

        if (!(blockState instanceof Sign)) {
            MessageManager.sendMessage(player, "&cYou must be looking at a sign.");
            return;
        }

        if (!leaderboard.getSignLocations().contains(block.getLocation())) {
            MessageManager.sendMessage(player, "&cThis leaderboard does not contain this sign.");
            return;
        }
        
        leaderboard.getSignLocations().remove(block.getLocation());
        leaderboard.save();

        MessageManager.sendMessage(player, "&aYou have removed this sign from the leaderboard.");
    }
}
