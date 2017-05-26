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
public class LeaderboardCreateCommand extends BaseCommand {

    @Command(name = "leaderboard.create", aliases = {"leaderboard.new"}, permission = "carbyne.command.leaderboard")
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();
        CommandSender sender = command.getSender();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&cUsage: /leaderboard");
            return;
        }

        if (getLeaderboardManager().getLeaderboard(args[0]) != null) {
            MessageManager.sendMessage(sender, "&cThere is already a leaderboard using this ID.");
            return;
        }

        Leaderboard leaderboard = new Leaderboard(args[0]);
        getLeaderboardManager().getLeaderboards().add(leaderboard);

        MessageManager.sendMessage(sender, "&aSuccessfully created a new leaderboard with the ID \'&b" + leaderboard.getBoardId() + "&a\'.");
    }
}
