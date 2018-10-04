package com.medievallords.carbyne.portals.commands;

import com.medievallords.carbyne.portals.Portal;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class PortalInfoCommands extends BaseCommand {

    @Command(name = "portal.list", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        StringBuilder builder = new StringBuilder();
        for (String s : StaticClasses.portalManager.getPortals().keySet()) {
            builder.append("&6" + s + "&7, ");
        }

        MessageManager.sendMessage(player, "&aPortals:");
        MessageManager.sendMessage(player, builder.toString());
    }

    @Command(name = "portal.near", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand2(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/portal near <radius>");
            return;
        }

        double toCheck;
        try {
            toCheck = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(player, "&cThe radius must be a number.");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (Portal portal : StaticClasses.portalManager.getPortals().values()) {
            if (portal.getPosition1().getWorld().equals(player.getLocation().getWorld())) {
                double distance = portal.getPosition1().distance(player.getLocation());
                if (distance < toCheck) {
                    builder.append("&6" + portal.getName() + " &c(" + distance + ")&7, ");
                }
            }
        }

        MessageManager.sendMessage(player, "&aPortals:");
        MessageManager.sendMessage(player, builder.toString());
    }
}
