package com.medievallords.carbyne.dungeons.dungeons.commands;

import com.medievallords.carbyne.dungeons.dungeons.Dungeon;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DungeonEditCommand extends BaseCommand {

//    @Command(name = "dungeon.edit", permission = "dungeons.administrator", inGameOnly = true)
//    public void execute(CommandArgs commandArgs) {
//        Player player = commandArgs.getPlayer();
//        String[] args = commandArgs.getArgs();
//
//        if (args.length != 1) {
//            MessageManager.sendMessage(player, "&cUsage: &7/dungeon edit <dungeon>");
//            return;
//        }
//
//        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(args[0]);
//        if (dungeon == null) {
//            MessageManager.sendMessage(player, "&cCould not find a dungeon with that name");
//            return;
//        }
//    }

    @Command(name = "dungeon.reload", permission = "dungeons.administrator")
    public void a(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();

        StaticClasses.dungeonHandler.reload();
        MessageManager.sendMessage(sender, "&aDungeons have been reloaded.");
    }
}
