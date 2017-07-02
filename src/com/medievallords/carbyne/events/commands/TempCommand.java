package com.medievallords.carbyne.events.commands;

import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-06-16
 * for the Carbyne project.
 */
public class TempCommand extends BaseCommand {

    @Command(name = "hohengrothevent.start", aliases = {"he.s"}, permission = "tempcommand.test", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        getCarbyne().getHohengrothSchedule().scheduleEvent();

    }
}
