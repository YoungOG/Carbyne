package com.medievallords.carbyne.quests.commands;

import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class QuestInfoCommand extends BaseCommand {

    @Command(name = "quest.info", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        StaticClasses.questHandler.openQuestInfo(player);
    }
}
