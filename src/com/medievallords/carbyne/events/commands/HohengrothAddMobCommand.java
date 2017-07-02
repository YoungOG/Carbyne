package com.medievallords.carbyne.events.commands;

import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-06-22.
 *
 */
public class HohengrothAddMobCommand extends BaseCommand {

    @Command(name = "hohengrothevent.addmob", aliases = {"he.am"}, permission = "hohengroth.addmob", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();
    }
}
