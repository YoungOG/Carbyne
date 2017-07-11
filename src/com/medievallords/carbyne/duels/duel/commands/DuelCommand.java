package com.medievallords.carbyne.duels.duel.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-04-02.
 * for the Carbyne project.
 */
public class DuelCommand extends BaseCommand {

    @Command(name = "duel")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        MessageManager.sendMessage(player, "&a/duel bet <amount> &7|| Set your bet");
        MessageManager.sendMessage(player, "&a/duel accept &7|| Accept a duel");
        MessageManager.sendMessage(player, "&a/duel decline &7|| Decline a duel");
        MessageManager.sendMessage(player, "&a/duel squad &7|| Request and accept a squad fight");

    }
}
