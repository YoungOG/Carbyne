package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Williams on 2017-06-13
 * for the Carbyne project.
 */
public class OnLoginCommand extends BaseCommand implements Listener{

    public HashMap<String, List<String>> commandsToRun = new HashMap<>();

    public OnLoginCommand() {
        ConfigurationSection cps = getCarbyne().getWeteFileConfiguration().getConfigurationSection("OnLoginCommands");
        if (cps != null) {
            for (String player : cps.getKeys(false)) {
                List<String> commands = cps.getStringList(player + ".Commands");

                if (commands != null) {
                    commandsToRun.put(player, commands);
                }
            }
        } else {
            getCarbyne().getWeteFileConfiguration().createSection("OnLoginCommands");

            try {
                getCarbyne().getWeteFileConfiguration().save(getCarbyne().getWeteFile());
                getCarbyne().setWeteFileConfiguration(YamlConfiguration.loadConfiguration(getCarbyne().getWeteFile()));
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Could not save and load wete.yml");
            }
        }
    }

    @Command(name = "onlogin", aliases = {"onlog", "onl", "ologin", "login"}, permission = "carbyne.commands.onlogin", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (getCarbyne().getWeteFileConfiguration().getConfigurationSection("OnLoginCommands") == null) {
            getCarbyne().getWeteFileConfiguration().createSection("OnLoginCommands");

            try {
                getCarbyne().getWeteFileConfiguration().save(getCarbyne().getWeteFile());
                getCarbyne().setWeteFileConfiguration(YamlConfiguration.loadConfiguration(getCarbyne().getWeteFile()));
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Could not save and load wete.yml");
            }
        }

        if (args.length < 1) {
            MessageManager.sendMessage(player, "&cUsage: &6/onlogin <player> <commandToRun>");
            return;
        }

        String command = "";
        String toRunFor = args[0];

        for (int i = 1; i < args.length; i++) {
            command = command + args[i] + " ";
        }

        if (toRunFor == null || command == "") {

            if (toRunFor == null) {
                MessageManager.sendMessage(player, "Player was not found");
                return;
            }

            if (command == "") {
                MessageManager.sendMessage(player, "Command cannot be nothing");
                return;
            }
        }

        if (!commandsToRun.containsKey(player.getName())) {
            List<String> commands = new ArrayList<>();
            commands.add(command);
            commandsToRun.put(toRunFor, commands);

            ConfigurationSection cps = getCarbyne().getWeteFileConfiguration().getConfigurationSection("OnLoginCommands." + toRunFor);

            if (cps == null) {
                getCarbyne().getWeteFileConfiguration().getConfigurationSection("OnLoginCommands").createSection(toRunFor);
            }

            getCarbyne().getWeteFileConfiguration().set("OnLoginCommands." + toRunFor + ".Commands", commands);

            MessageManager.sendMessage(player, "&6Command has been added to &b" + toRunFor);

        } else {
            List<String> commands = commandsToRun.get(player.getName());
            if (!commands.contains(command)) {
                commands.add(command);
            }

            commandsToRun.put(toRunFor, commands);

            ConfigurationSection cps = getCarbyne().getWeteFileConfiguration().getConfigurationSection("OnLoginCommands." + toRunFor);

            if (cps == null) {
                getCarbyne().getWeteFileConfiguration().getConfigurationSection("OnLoginCommands").createSection(toRunFor);
            }

            getCarbyne().getWeteFileConfiguration().set("OnLoginCommands." + toRunFor + ".Commands", commands);

            MessageManager.sendMessage(player, "&6Command has been added to &b" + toRunFor);
        }

        try {
            getCarbyne().getWeteFileConfiguration().save(getCarbyne().getWeteFile());
            getCarbyne().setWeteFileConfiguration(YamlConfiguration.loadConfiguration(getCarbyne().getWeteFile()));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not save and load wete.yml");
        }
    }

    @Command(name = "checkonlogin", aliases = {"conlog", "conl", "conlogin", "clogin"}, permission = "carbyne.commands.onlogin", inGameOnly = true)
    public void onCommandCheck(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &6/checkonlogin <player>");
            return;
        }

        if (!commandsToRun.containsKey(args[0])) {
            MessageManager.sendMessage(player, "&b" + args[0] + "&6 has no scheduled commands");
            return;
        } else {
            for (int i = 0; i < commandsToRun.get(args[0]).size(); i++) {
                String command = commandsToRun.get(args[0]).get(i);
                MessageManager.sendMessage(player, "&aIndex: &6" + i + " &7| &aCommand: &6" + command);
            }
        }
    }

    @Command(name = "removeonlogin", aliases = {"ronlog", "ronl", "ronlogin", "rlogin"}, permission = "carbyne.commands.onlogin", inGameOnly = true)
    public void onCommandRemove(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&cUsage: &6/removeonlogin <player> <index/clear>");
            return;
        }

        String toRemoveFrom = args[0];

        if (toRemoveFrom == null) {
            MessageManager.sendMessage(player,"&cCould not find that player");
            return;
        }

        if (!commandsToRun.containsKey(toRemoveFrom)) {
            MessageManager.sendMessage(player, "That player has no scheduled commands");
            return;
        }

        if (args[1].equalsIgnoreCase("clear")) {
            commandsToRun.remove(toRemoveFrom);
            getCarbyne().getWeteFileConfiguration().set("OnLoginCommands." + toRemoveFrom, null);
            MessageManager.sendMessage(player, "&6All commands for player &b" + toRemoveFrom + " &6has been deleted");

        } else {
            try {
                commandsToRun.get(toRemoveFrom).remove(Integer.parseInt(args[1]));

                if (commandsToRun.get(toRemoveFrom).size() <= 0) {
                    getCarbyne().getWeteFileConfiguration().set("OnLoginCommands." + toRemoveFrom, null);
                    MessageManager.sendMessage(player, "&6All commands for player &b" + toRemoveFrom + " &6has been deleted");
                    return;
                }

                ConfigurationSection cps = getCarbyne().getWeteFileConfiguration().getConfigurationSection("OnLoginCommands." + toRemoveFrom);

                if (cps == null) {
                    getCarbyne().getWeteFileConfiguration().getConfigurationSection("OnLoginCommands").createSection(toRemoveFrom);
                }

                getCarbyne().getWeteFileConfiguration().set("OnLoginCommands." + toRemoveFrom + ".Commands", commandsToRun.get(toRemoveFrom));

                MessageManager.sendMessage(player, "&6Command index &a" + args[1]  + " &6for player &b" + toRemoveFrom + " &6has been deleted");

            } catch (NumberFormatException e) {
                MessageManager.sendMessage(player, "&cThe index must be a number, &6/removeonlogin <player> <index/clear>");
            }
        }

        try {
            getCarbyne().getWeteFileConfiguration().save(getCarbyne().getWeteFile());
            YamlConfiguration.loadConfiguration(getCarbyne().getWeteFile());
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not save and load wete.yml");
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {

        new BukkitRunnable() {
            @Override
            public void run() {

                if (commandsToRun.containsKey(event.getPlayer().getName())) {
                    for (String command : commandsToRun.get(event.getPlayer().getName())) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
                    }
                    commandsToRun.remove(event.getPlayer().getName());
                    getCarbyne().getWeteFileConfiguration().set("OnLoginCommands." + event.getPlayer().getName(), null);

                    try {
                        getCarbyne().getWeteFileConfiguration().save(getCarbyne().getWeteFile());
                        YamlConfiguration.loadConfiguration(getCarbyne().getWeteFile());
                    } catch (IOException e) {
                        Bukkit.getLogger().log(Level.WARNING, "Could not save and load wete.yml");
                    }
                }

            }
        }.runTaskLater(getCarbyne(), 20);
    }
}
