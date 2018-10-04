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

public class PortalRemoveCommand extends BaseCommand {

    @Command(name = "portal.remove", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/portal remove <name>");
            return;
        }

        Portal portal = StaticClasses.portalManager.getPortal(args[0]);
        if (portal == null) {
            MessageManager.sendMessage(player, "&cCould not find a portal with that name.");
            return;
        }

        StaticClasses.portalManager.getPortals().remove(portal.getName().toLowerCase());

        ConfigurationSection cs = Carbyne.getInstance().getPortalsFileConfiguration().getConfigurationSection("Portals");
        cs.set(portal.getName(), null);

        try {
            Carbyne.getInstance().getPortalsFileConfiguration().save(Carbyne.getInstance().getPortalsFile());
            MessageManager.sendMessage(player, "&cPortal has been removed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
