package com.medievallords.carbyne.worldhandler.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeleportWorldCommand extends BaseCommand {

    @Command(name = "carbyneworld.teleport", aliases = {"cw.tp"}, inGameOnly = true, permission = "carbyne.world.teleport")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/carbyneworld teleport <world>");
            return;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            MessageManager.sendMessage(player, "&cCould not find that world.");
            return;
        }

        player.teleport(world.getSpawnLocation());
        MessageManager.sendMessage(player, "&aYou have teleported to world&7: " + world.getName() + "&a.");
    }
}
