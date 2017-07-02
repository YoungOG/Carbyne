package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class ClearChatCommand extends BaseCommand {

    @Command(name = "clearchat", aliases = {"cc"}, permission = "carbyne.staff")
    public void onCommand(CommandArgs commandArgs) {
        for (int i = 0; i < 125; ++i) {
            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (!all.hasPermission("carbyne.staff")) {
                    MessageManager.sendMessage(all, " ");
                }
            }
        }

        MessageManager.broadcastMessage("&aThe chat has been cleared.");
    }
}
