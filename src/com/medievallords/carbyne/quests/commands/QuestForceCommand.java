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

public class QuestForceCommand extends BaseCommand {

    @Command(name = "quest.force", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/quest force <player>");
            return;
        }

        Player toForce = Bukkit.getPlayer(args[0]);
        if (toForce == null) {
            MessageManager.sendMessage(player, "&cCould not find that player.");
            return;
        }

        for (Quest current : StaticClasses.questHandler.getQuests(toForce.getUniqueId())) {
            current.removePlayer(toForce);
        }

        Profile profile = StaticClasses.profileManager.getProfile(toForce.getUniqueId());
        profile.setForcedQuests(StaticClasses.questHandler.getForcedQuestsByName());

        Quest quest = StaticClasses.questHandler.findForcedQuest(profile);
        if (quest != null) {
            MessageManager.sendMessage(player, "&aAll forced quests have been reset for player&7: " + toForce.getName());
        } else {
            MessageManager.sendMessage(player, "&cFailed to find forced quest for player&7: " + toForce.getName());
        }
    }
}
