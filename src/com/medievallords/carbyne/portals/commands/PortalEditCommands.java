package com.medievallords.carbyne.portals.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.portals.Portal;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PortalEditCommands extends BaseCommand {

    @Command(name = "portal.setposition", aliases = {"portal.setpos", "portal.sp"}, inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&cUsage: &7/portal sp <name> <index>");
            return;
        }

        Portal portal = StaticClasses.portalManager.getPortal(args[0]);
        if (portal == null) {
            MessageManager.sendMessage(player, "&cCould not find a portal with that name.");
            return;
        }

        Location lookingAt = player.getTargetBlock((Set<Material>) null, 10).getLocation();

        int index;
        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(player, "&cThe index must be either 1 or 2.");
            return;
        }

        if (index == 1) {
            ConfigurationSection cs = Carbyne.getInstance().getPortalsFileConfiguration().getConfigurationSection("Portals").getConfigurationSection(portal.getName());
            if (cs != null) {
                cs.set("Position1", LocationSerialization.serializeLocation(lookingAt));
            }

            portal.setPosition1(lookingAt);
        } else if (index == 2) {
            ConfigurationSection cs = Carbyne.getInstance().getPortalsFileConfiguration().getConfigurationSection("Portals").getConfigurationSection(portal.getName());
            if (cs != null) {
                cs.set("Position2", LocationSerialization.serializeLocation(lookingAt));
            }

            portal.setPosition2(lookingAt);
        } else {
            MessageManager.sendMessage(player, "&cThe index must be either 1 or 2.");
            return;
        }

        try {
            Carbyne.getInstance().getPortalsFileConfiguration().save(Carbyne.getInstance().getPortalsFile());
            MessageManager.sendMessage(player, "&aA portal position has been set.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Command(name = "portal.target", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand3(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/portal target <name>");
            return;
        }

        Portal portal = StaticClasses.portalManager.getPortal(args[0]);
        if (portal == null) {
            MessageManager.sendMessage(player, "&cCould not find a portal with that name.");
            return;
        }

        portal.setTargetLocation(player.getLocation());

        ConfigurationSection cs = Carbyne.getInstance().getPortalsFileConfiguration().getConfigurationSection("Portals").getConfigurationSection(portal.getName());
        if (cs != null) {
            cs.set("TargetLocation", LocationSerialization.serializeLocation(portal.getTargetLocation()));
        }

        try {
            Carbyne.getInstance().getPortalsFileConfiguration().save(Carbyne.getInstance().getPortalsFile());
            MessageManager.sendMessage(player, "&aThe target location has been set.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
