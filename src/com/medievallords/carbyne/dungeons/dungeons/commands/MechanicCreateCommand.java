package com.medievallords.carbyne.dungeons.dungeons.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.Dungeon;
import com.medievallords.carbyne.dungeons.mechanics.Mechanic;
import com.medievallords.carbyne.dungeons.triggers.Trigger;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MechanicCreateCommand extends BaseCommand {

    @Command(name = "dungeon.mechanic.create", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length < 3) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon mechanic create <dungeon> <trigger> <mechanicName> [arguments]");
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
            MessageManager.sendMessage(player, "&A trigger with that name could not be found.");
            return;
        }

        String mechanicName = args[2];

        List<String> data = new ArrayList<>();
        for (int i = 3; i < args.length; i++) {
            String fullArgument = args[i];
            String[] splitArgument = fullArgument.split("=");

            if (splitArgument.length != 2)
                continue;

            data.add(fullArgument
                    .replace("%loc", LocationSerialization.serializeLocation(player.getLocation()))
                    .replace("%tar", LocationSerialization.serializeLocation(player.getTargetBlock((Set<Material>) null, 30).getLocation())));
        }

        if (data.isEmpty()) {
            MessageManager.sendMessage(player, "&cYou need to provide data for the mechanic.");
            return;
        }

        Mechanic mechanic = Mechanic.getMechanic(mechanicName, data);
        if (mechanic == null) {
            MessageManager.sendMessage(player, "&cFailed to create the mechanic. Check your data.");
            return;
        }

        trigger.getMechanics().add(mechanic);

        ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName() + ".Triggers");

        if (section == null)
            section = Carbyne.getInstance().getDungeonsFileConfiguration().createSection("Dungeons." + dungeon.getName() + ".Triggers");

        ConfigurationSection triggerSection = section.getConfigurationSection(trigger.getName());
        ConfigurationSection mechanicSection;

        if (!triggerSection.isConfigurationSection("Mechanics"))
            mechanicSection = triggerSection.createSection("Mechanics");
        else
            mechanicSection = triggerSection.getConfigurationSection("Mechanics");

        mechanicSection.set(mechanicName.toLowerCase(), data);

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
            MessageManager.sendMessage(player, "&aYou have created a new mechanic called &5" + mechanicName + "&a.");
        } catch (IOException e) {
            MessageManager.sendMessage(player, "&cFailed to create a new trigger. Check your arguments.");
            e.printStackTrace();
        }
    }
}
