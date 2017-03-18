package com.medievallords.carbyne.controlpoints.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by Williams on 2017-03-16.
 * for the Carbyne project.
 */
public class ControlReloadCommand extends BaseCommand {

    @Command(name = "controlpoint.reload", permission = "carbyne.controlpoint.admin", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /cp");
            return;
        }

        try {
            Carbyne.getInstance().getControlPointConfiguration().save(Carbyne.getInstance().getControlPointFile());
            YamlConfiguration.loadConfiguration(Carbyne.getInstance().getControlPointFile());
            Carbyne.getInstance().getControlManager().loadControlPoints();
            MessageManager.sendMessage(player, "Configuration reloaded and points loaded");
        } catch (IOException e) {
            Bukkit.getServer().getLogger().log(Level.WARNING, "Could not load configuration file");
        }
    }
}
