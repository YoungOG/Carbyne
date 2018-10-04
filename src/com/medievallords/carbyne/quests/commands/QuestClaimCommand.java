package com.medievallords.carbyne.quests.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.quests.Quest;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class QuestClaimCommand extends BaseCommand {

    @Command(name = "takequest", permission = "carbyne.administrator")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            return;
        }

        Player player = Bukkit.getServer().getPlayer(args[0]);
        if (player == null) {
            return;
        }

        String questName = args[1];

        Quest quest = StaticClasses.questHandler.getQuest(questName);
        if (quest == null) {
            return;
        }

        List<Quest> quests = StaticClasses.questHandler.getQuests(player.getUniqueId());

        for (Quest q : quests) {
            if (q == quest) {
                quest.completeQuest(player);
                return;
            }
        }

        if (player.hasPermission("carbyne.donator")) {
            if (quests.size() >= 4) {
                MessageManager.sendMessage(player, "&cYou cannot have more than 3 dormantQuests at a time.");
                return;
            }
        } else {
            if (quests.size() >= 2) {
                MessageManager.sendMessage(player, "&cYou cannot have more than 3 dormantQuests at a time.");
                return;
            }
        }

        quest.takeQuest(player);
    }
}
