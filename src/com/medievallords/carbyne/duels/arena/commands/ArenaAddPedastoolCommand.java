package com.medievallords.carbyne.duels.arena.commands;

import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by Calvin on 3/17/2017
 * for the Carbyne project.
 */
public class ArenaAddPedastoolCommand extends BaseCommand {

    @Command(name = "arena.addpedastool", aliases = {"arena.setped", "arena.ped", "arena.pedastool", "arena.setpedastool"}, permission = "carbyne.commands.arena")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&cUsage: /arena");
            return;
        }

        Arena arena = getDuelManager().getArena(args[1]);

        if (arena == null) {
            MessageManager.sendMessage(player, "&cCould not find an arena with the ID \'" + args[1] + "\'.");
            return;
        }

        Location location = player.getTargetBlock((Set<Material>) null, 10).getLocation();

        if (location.getBlock().getType().toString().contains("PLATE")) {
            MessageManager.sendMessage(player, "&cYou must be looking at a pressure plate.");
        }

        try {
            int i = Integer.parseInt(args[0]);

            for (Location loc : arena.getPedastoolLocations()) {
                Bukkit.broadcastMessage("Location: " + loc.toString());
            }

            if (i == 1) {
                arena.getPedastoolLocations()[0] = location;
                MessageManager.sendMessage(player, "&aYou have set pedastool point 1 for the arena \'&b" + arena.getArenaId() + "&a\'.");
            } else if (i == 2) {
                arena.getPedastoolLocations()[1] = location;
                MessageManager.sendMessage(player, "&aYou have set pedastool point 2 for the arena \'&b" + arena.getArenaId() + "&a\'.");
            } else {
                MessageManager.sendMessage(player, "&cYou must input either 1 or 2 for the pedastool point.");
            }

            arena.save();
        } catch (NumberFormatException ignored) {
            MessageManager.sendMessage(player, "&cYou must input either 1 or 2 for the pedastool point.");
        }
    }
}
