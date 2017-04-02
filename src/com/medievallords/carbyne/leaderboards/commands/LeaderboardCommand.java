package com.medievallords.carbyne.leaderboards.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

/**
 * Created by Calvin on 1/23/2017
 * for the Carbyne-Gear project.
 */
public class LeaderboardCommand {

    @Command(name = "leaderboard", permission = "carbyne.command.leaderboard", aliases = {"leaderboards", "boards", "board"})
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();
        CommandSender sender = command.getSender();

        if (command.getArgs().length == 0) {
            MessageManager.sendMessage(sender, "&7============[ &bLeaderboard &7]============");
            MessageManager.sendMessage(sender, "&a/leaderboard create <boardID>");
            MessageManager.sendMessage(sender, "&a/leaderboard remove <boardID>");
            MessageManager.sendMessage(sender, "&a/leaderboard setPrimarySign <boardID>");
            MessageManager.sendMessage(sender, "&a/leaderboard addSign <boardID>");
            MessageManager.sendMessage(sender, "&a/leaderboard delSign <boardID>");
            MessageManager.sendMessage(sender, "&a/leaderboard addHead <boardID>");
            MessageManager.sendMessage(sender, "&a/leaderboard delHead <boardID>");
            MessageManager.sendMessage(sender, "&a/leaderboard list");
        }
    }
}
