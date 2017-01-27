package com.medievallords.carbyne.scoreboard;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScoreboardCommands implements CommandExecutor {

    private Carbyne carbyne = Carbyne.getInstance();
    private GearManager gearManager = carbyne.getGearManager();
    private ScoreboardHandler scoreboardHandler = carbyne.getScoreboardHandler();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("scoreboard") || cmd.getName().equalsIgnoreCase("sb") || cmd.getName().equalsIgnoreCase("hud")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("toggle")) {
                    Player player = (Player) sender;

                    if (!scoreboardHandler.getPlayerTasks().containsKey(player.getUniqueId())) {
                        scoreboardHandler.addTask(player);
                    } else {
                        scoreboardHandler.getPlayerTasks().get(player.getUniqueId()).cancel();
                        scoreboardHandler.getPlayerTasks().remove(player.getUniqueId());
                        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    }
                } else {
                    MessageManager.sendMessage(sender, "&c/hud toggle");
                    return false;
                }
            } else {
                MessageManager.sendMessage(sender, "&c/hud toggle");
                return false;
            }
        }

        return true;
    }
}
