package com.medievallords.carbyne.leaderboards.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;

/**
 * Created by Calvin on 1/23/2017
 * for the Carbyne-Gear project.
 */
public class LeaderboardCommand {

    @Command(name = "leaderboard", permission = "carbyne.command.leaderboard", aliases = {"leaderboards", "boards", "board"})
    public void onCommand(CommandArgs command) {
        if (command.getArgs().length == 0) {
            MessageManager.sendMessage(command.getSender(), "");
        }
    }
}
