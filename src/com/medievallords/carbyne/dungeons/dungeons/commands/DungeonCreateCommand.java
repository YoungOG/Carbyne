package com.medievallords.carbyne.dungeons.dungeons.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-09-27.
 */

public class DungeonCreateCommand extends BaseCommand {

    @Command(name = "dungeon.create", permission = "dungeon.commands.creator", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon create <name>");
            return;
        }

        String name = args[0];

        if (StaticClasses.dungeonHandler.getDungeon(name) != null) {
            MessageManager.sendMessage(player, "&cThere is already a dungeon with that name");
            return;
        }

        StaticClasses.dungeonHandler.createDungeon(name);

        MessageManager.sendMessage(player, "&6Dungeon &b" + name + "&6 has been created.");
    }
}
