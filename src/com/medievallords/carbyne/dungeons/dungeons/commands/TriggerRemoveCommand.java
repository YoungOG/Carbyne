package com.medievallords.carbyne.dungeons.dungeons.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.Dungeon;
import com.medievallords.carbyne.dungeons.triggers.Trigger;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;

public class TriggerRemoveCommand extends BaseCommand {

    @Command(name = "dungeon.trigger.remove", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon trigger remove <dungeon> <name>");
            return;
        }

        String dungeonName = args[0];

        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(dungeonName);

        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cA dungeon with that name could not be found.");
            return;
        }

        Trigger trigger = dungeon.getTrigger(args[1]);

        if (trigger == null) {
            MessageManager.sendMessage(player, "&cA trigger with that name could not be found.");
            return;
        }

        ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName() + ".Triggers");

        if (section == null)
            section = Carbyne.getInstance().getDungeonsFileConfiguration().createSection("Dungeons." + dungeon.getName() + ".Triggers");

        section.set(trigger.getName(), null);

        dungeon.getTriggers().remove(trigger);

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
            MessageManager.sendMessage(player, "&aYou have removed trigger called &5" + trigger.getName() + "&a from the &b" + dungeon.getName() + " &adungeon.");
        } catch (IOException e) {
            MessageManager.sendMessage(player, "&cFailed to remove the trigger.");
            e.printStackTrace();
        }
    }
}
