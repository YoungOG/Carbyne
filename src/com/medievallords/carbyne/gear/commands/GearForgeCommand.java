package com.medievallords.carbyne.gear.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GearForgeCommand extends BaseCommand {

    @Command(name = "forge.add", permission = "carbyne.administrator", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        Location lookingAt = player.getTargetBlock(null, 10).getLocation();
        if (StaticClasses.gearManager.getForgeLocations().contains(lookingAt)) {
            MessageManager.sendMessage(player, "&cThat block is already a forge.");
            return;
        }

        StaticClasses.gearManager.getForgeLocations().add(lookingAt);
        ConfigurationSection section = Carbyne.getInstance().getGearFileConfiguration();

        List<String> currentList = section.getStringList("ForgeLocations");
        currentList.add(LocationSerialization.serializeLocation(lookingAt));
        section.set("ForgeLocations", currentList);

        try {
            Carbyne.getInstance().getGearFileConfiguration().save(Carbyne.getInstance().getGearFile());
            MessageManager.sendMessage(player, "&aThe location you are looking at has been set.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Command(name = "forge.del", permission = "carbyne.administrator", inGameOnly = true)
    public void onCommand2(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        Location lookingAt = player.getTargetBlock(null, 10).getLocation();
        if (!StaticClasses.gearManager.getForgeLocations().contains(lookingAt)) {
            MessageManager.sendMessage(player, "&cThat block is not a forge.");
            return;
        }

        StaticClasses.gearManager.getForgeLocations().remove(lookingAt);
        ConfigurationSection section = Carbyne.getInstance().getGearFileConfiguration();

        List<String> currentList = new ArrayList<>();
        for (Location loc : StaticClasses.gearManager.getForgeLocations()) {
            currentList.add(LocationSerialization.serializeLocation(loc));
        }

        currentList.add(LocationSerialization.serializeLocation(lookingAt));
        section.set("ForgeLocations", currentList);

        try {
            Carbyne.getInstance().getGearFileConfiguration().save(Carbyne.getInstance().getGearFile());
            MessageManager.sendMessage(player, "&cThe location you are looking at has been removed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
