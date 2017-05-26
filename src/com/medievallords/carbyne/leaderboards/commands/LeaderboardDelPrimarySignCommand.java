package com.medievallords.carbyne.leaderboards.commands;

import com.medievallords.carbyne.leaderboards.Leaderboard;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 1/23/2017
 * for the Carbyne-Gear project.
 */
public class LeaderboardDelPrimarySignCommand extends BaseCommand {

    @Command(name = "leaderboard.delprimarysign", aliases = {"leaderboard.delps", "leaderboard.delprimesign", "leaderboard.delmainsign"}, permission = "carbyne.command.leaderboard", inGameOnly = true)
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

        leaderboard.setPrimarySignLocation(null);
        leaderboard.getBukkitTask().cancel();
        leaderboard.save();

        MessageManager.sendMessage(player, "&aThe primary sign location has been removed.");
    }
}
