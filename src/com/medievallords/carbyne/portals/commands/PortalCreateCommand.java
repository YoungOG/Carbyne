package com.medievallords.carbyne.portals.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.portals.Portal;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;

public class PortalCreateCommand extends BaseCommand {

    @Command(name = "portal.create", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/portal create <name>");
            return;
        }

        if (StaticClasses.portalManager.getPortal(args[0]) != null) {
            MessageManager.sendMessage(player, "&cThere is already a portal with that name.");
            return;
        }

        Portal portal = new Portal(args[0]);
        StaticClasses.portalManager.getPortals().put(args[0].toLowerCase(), portal);

        ConfigurationSection cs = Carbyne.getInstance().getPortalsFileConfiguration().getConfigurationSection("Portals").createSection(args[0]);
        cs.set("Locations", new ArrayList<>());
        cs.set("TargetLocation", "");

        try {
            Carbyne.getInstance().getPortalsFileConfiguration().save(Carbyne.getInstance().getPortalsFile());
            MessageManager.sendMessage(player, "&aPortal has been created.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
