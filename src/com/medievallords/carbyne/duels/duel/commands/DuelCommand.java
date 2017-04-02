package com.medievallords.carbyne.duels.duel.commands;

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

    }
}
