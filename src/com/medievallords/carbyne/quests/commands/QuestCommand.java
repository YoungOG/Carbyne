package com.medievallords.carbyne.quests.commands;


import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-10-09.
 */

public class QuestCommand extends BaseCommand {

    @Command(name = "quests", permission = "carbyne.administrator")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            return;
        }

        Player player = Bukkit.getServer().getPlayer(args[0]);
        if (player == null) {
            return;
        }

        player.openInventory(StaticClasses.questHandler.getQuestInventory(player));
    }
}
