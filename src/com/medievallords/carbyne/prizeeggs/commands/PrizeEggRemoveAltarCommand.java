package com.medievallords.carbyne.prizeeggs.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.prizeeggs.PrizeEggManager;
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
import java.util.List;
import java.util.Set;

public class PrizeEggRemoveAltarCommand extends BaseCommand {

    @Command(name = "prizeeggs.remove", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        //"&cUsage: &7prizeeggs remove");

        PrizeEggManager prizeEggManager = StaticClasses.prizeEggManager;

        Location lookingAt = player.getTargetBlock((Set<Material>) null, 10).getLocation();
        if (!prizeEggManager.getAltarLocations().contains(lookingAt)) {
            MessageManager.sendMessage(player, "&cThat location is not set.");
            return;
        }

        prizeEggManager.getAltarLocations().remove(lookingAt);
        ConfigurationSection section = Carbyne.getInstance().getPrizeEggFileConfiguration();

        List<String> currentList = new ArrayList<>();
        for (Location loc : prizeEggManager.getAltarLocations()) {
            currentList.add(LocationSerialization.serializeLocation(loc));
        }

        currentList.add(LocationSerialization.serializeLocation(lookingAt));
        section.set("Locations", currentList);

        try {
            Carbyne.getInstance().getPrizeEggFileConfiguration().save(Carbyne.getInstance().getPrizeEggFile());
            MessageManager.sendMessage(player, "&cThe location you are looking at has been removed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
