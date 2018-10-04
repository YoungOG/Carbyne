package com.medievallords.carbyne.dungeons.dungeons.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.Dungeon;
import com.medievallords.carbyne.dungeons.mechanics.Mechanic;
import com.medievallords.carbyne.dungeons.triggers.Trigger;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;

public class MechanicRemoveCommand extends BaseCommand {

    @Command(name = "dungeon.mechanic.remove", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 3) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon mechanic remove <dungeon> <triggerName> <mechanicName>");
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

        Mechanic mechanic = trigger.getMechanic(args[2]);

        if (mechanic == null) {
            MessageManager.sendMessage(player, "&cA mechanic with that type could not be found.");
            return;
        }

        ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName() + ".Triggers");

        if (section == null)
            section = Carbyne.getInstance().getDungeonsFileConfiguration().createSection("Dungeons." + dungeon.getName() + ".Triggers");

        ConfigurationSection triggerSection = section.getConfigurationSection(trigger.getName());
        ConfigurationSection mechanicSection = triggerSection.getConfigurationSection("Mechanics");

        mechanicSection.set(mechanic.getType(), null);

        trigger.getMechanics().remove(mechanic);

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
            MessageManager.sendMessage(player, "&aYou have removed mechanic with the type &5" + mechanic.getType() + "&a from the &b" + dungeon.getName() + " &adungeon.");
        } catch (IOException e) {
            MessageManager.sendMessage(player, "&cFailed to remove the mechanic.");
            e.printStackTrace();
        }
    }
}
