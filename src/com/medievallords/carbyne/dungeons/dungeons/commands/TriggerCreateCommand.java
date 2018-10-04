package com.medievallords.carbyne.dungeons.dungeons.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.Dungeon;
import com.medievallords.carbyne.dungeons.triggers.DistanceTrigger;
import com.medievallords.carbyne.dungeons.triggers.InteractTrigger;
import com.medievallords.carbyne.dungeons.triggers.MobTrigger;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TriggerCreateCommand extends BaseCommand {

//    String replacedArgument = fullArgument
//            .replace("%loc", LocationSerialization.serializeLocation(player.getLocation()))
//            .replace("%tar", LocationSerialization.serializeLocation(player.getTargetBlock((Set<Material>) null, 30).getLocation()));

    @Command(name = "dungeon.trigger.create", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length < 3) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon trigger create <dungeon> <name> <type> [arguments]");
            return;
        }

        String dungeonName = args[0];

        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(dungeonName);

        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cA dungeon with that name could not be found.");
            return;
        }

        String triggerName = args[1];

        if (dungeon.getTrigger(triggerName) != null) {
            MessageManager.sendMessage(player, "&cA trigger with that name already exists for the dungeon specified.");
            return;
        }

        String triggerType = args[2];
        Location targetLocation = player.getTargetBlock((Set<Material>) null, 30).getLocation();

        List<String> data = new ArrayList<>();
        for (int i = 3; i < args.length; i++) {
            String fullArgument = args[i];
            String[] splitArgument = fullArgument.split("=");

            if (splitArgument.length != 2)
                continue;

            data.add(fullArgument);
        }

        switch (triggerType.toLowerCase()) {
            case "distance":
                DistanceTrigger distanceTrigger = new DistanceTrigger(triggerName, targetLocation, new DungeonLineConfig(data));
                dungeon.getTriggers().add(distanceTrigger);
                break;
            case "interact":
                InteractTrigger interactTrigger = new InteractTrigger(triggerName, targetLocation, new DungeonLineConfig(data));
                dungeon.getTriggers().add(interactTrigger);
                break;
            case "mobdeath":
                MobTrigger mobTrigger = new MobTrigger(triggerName, targetLocation, new DungeonLineConfig(data));
                dungeon.getTriggers().add(mobTrigger);
                break;
            default:
                MessageManager.sendMessage(player, "&cCould not find a trigger using that type.");
                return;
        }

        ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName() + ".Triggers");

        if (section == null)
            section = Carbyne.getInstance().getDungeonsFileConfiguration().createSection("Dungeons." + dungeon.getName() + ".Triggers");

        ConfigurationSection triggerSection = section.createSection(triggerName);
        triggerSection.set("Location", LocationSerialization.serializeLocation(targetLocation));
        triggerSection.set("Data", data);
        triggerSection.set("Type", triggerType.toLowerCase());
        triggerSection.createSection("Mechanics");

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
            MessageManager.sendMessage(player, "&aYou have created a new trigger called &5" + triggerName + "&a with the type &b" + triggerType + "&a.");
        } catch (IOException e) {
            MessageManager.sendMessage(player, "&cFailed to create a new trigger. Check your arguments.");
            e.printStackTrace();
        }
    }
}
