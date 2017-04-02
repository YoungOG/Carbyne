package com.medievallords.carbyne.duels.arena.commands;

import com.medievallords.carbyne.crates.Crate;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Calvin on 3/17/2017
 * for the Carbyne project.
 */
public class ArenaListCommand extends BaseCommand {

    @Command(name = "arena.list", permission = "carbyne.commands.arena")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length != 0) {
            MessageManager.sendMessage(sender, "&cUsage: /arena");
            return;
        }

        if (getDuelManager().getArenas().size() <= 0) {
            MessageManager.sendMessage(sender, "&cThere are no available arenas to display.");
            return;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

                MessageManager.sendMessage(sender, "&aAvailable Arenas:");

                JSONMessage message = JSONMessage.create();
                for (int i = 0; i < getDuelManager().getArenas().size(); i++) {
                    if (i < getDuelManager().getArenas().size() - 1) {
                        Arena arena = getDuelManager().getArenas().get(i);

                        message.then(arena.getArenaId()).color(ChatColor.AQUA)
                                .tooltip(getMessageForArena(arena))
                                .then(", ").color(ChatColor.GRAY);
                    } else {
                        Arena arena = getDuelManager().getArenas().get(i);

                        message.then(arena.getArenaId()).color(ChatColor.AQUA)
                                .tooltip(getMessageForArena(arena));
                    }
                }

                message.send(player);
            } else {
                MessageManager.sendMessage(sender, "&aAvailable Arenas:");

                List<String> crateNames = new ArrayList<>();
                for (Crate crate : getCrateManager().getCrates()) {
                    crateNames.add(ChatColor.AQUA + crate.getName());
                }

                MessageManager.sendMessage(sender, crateNames.toString().replace("[", "").replace("]", "").replace(",", ChatColor.GRAY + ","));
            }
    }

    public JSONMessage getMessageForArena(Arena arena) {
        JSONMessage message2 = JSONMessage.create("");

        message2.then(ChatColor.translateAlternateColorCodes('&', "&aArena Id: &b" + arena.getArenaId()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aDuel Object: &b" + (arena.getDuel() != null ? arena.getDuel().getDuelStage() : "Null") + "\n"));
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aLobby Location: " + (arena.getLobbyLocation() != null ? "&bWorld: &b" + arena.getLobbyLocation().getWorld().getName() + "&a, X: &b" + arena.getLobbyLocation().getBlockX() + "&a, Y: &b" + arena.getLobbyLocation().getBlockY() + "&a, Z: &b" + arena.getLobbyLocation().getBlockZ() : "&bNull") + "\n"));
        message2.then("\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aPedastools(&b" + arena.getPedastoolLocations().length + "&a):") + "\n");

        int id = 0;

        if (arena.getPedastoolLocations() != null && arena.getPedastoolLocations().length > 0) {
            for (Location location : arena.getPedastoolLocations()) {
                if (location != null) {
                    id++;
                    message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
                }
            }

            message2.then("\n");
        }

        message2.then(ChatColor.translateAlternateColorCodes('&', " &aSpawn Points(&b" + arena.getSpawnPointLocations().length + "&a):") + "\n");

        if (arena.getSpawnPointLocations() != null && arena.getSpawnPointLocations().length > 0) {
            id = 0;

            for (Location location : arena.getSpawnPointLocations()) {
                if (location != null) {
                    id++;
                    message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
                }
            }
        }

        return message2;
    }
}
