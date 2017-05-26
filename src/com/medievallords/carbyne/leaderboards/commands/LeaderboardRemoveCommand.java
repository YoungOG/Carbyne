package com.medievallords.carbyne.leaderboards.commands;

import com.medievallords.carbyne.leaderboards.Leaderboard;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

/**
 * Created by Calvin on 1/23/2017
 * for the Carbyne-Gear project.
 */
public class LeaderboardRemoveCommand extends BaseCommand {

    @Command(name = "leaderboard.remove", aliases = {"leaderboard.delete", "leaderboard.del"}, permission = "carbyne.command.leaderboard")
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();
        CommandSender sender = command.getSender();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&cUsage: /leaderboard");
            return;
        }

        Leaderboard leaderboard = getLeaderboardManager().getLeaderboard(args[0]);

        if (leaderboard == null) {
            MessageManager.sendMessage(sender, "&cCould not find the requested leaderboard.");
            return;
        }

        leaderboard.getBukkitTask().cancel();
        getLeaderboardManager().getLeaderboards().remove(leaderboard);

        if (getCarbyne().getLeaderboardFileConfiguration().getConfigurationSection("Leaderboards").contains(leaderboard.getBoardId())) {
            getCarbyne().getLeaderboardFileConfiguration().getConfigurationSection("Leaderboards").set(leaderboard.getBoardId(), null);
        }

        MessageManager.sendMessage(sender, "&aThe leaderboard has been removed.");
    }
}
