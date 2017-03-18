package com.medievallords.carbyne.squads.commands;

import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-12
 * for the Carbyne project.
 */
public class SquadListCommand extends BaseCommand {

    @Command(name = "squad.list", aliases = {"i"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length != 0) {
            MessageManager.sendMessage(sender, "&cUsage: /squad");
            return;
        }

        if (getSquadManager().getSquads().size() <= 0) {
            MessageManager.sendMessage(sender, "&cThere are no available squads to display.");
            return;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            MessageManager.sendMessage(sender, "&aAvailable Squads:");

            JSONMessage message = JSONMessage.create("");

            for (int i = 0; i < getSquadManager().getSquads().size(); i++) {
                if (i < getSquadManager().getSquads().size() - 1) {
                    Squad squad = getSquadManager().getSquads().get(i);

                    Player leader = Bukkit.getServer().getPlayer(squad.getLeader());
                    if (leader == null) {
                        continue;
                    }

                    message.then(leader.getName()).color(ChatColor.AQUA)
                            .tooltip(getMessageForSquad(squad))
                            .then(", ").color(ChatColor.GRAY);
                } else {
                    Squad squad = getSquadManager().getSquads().get(i);

                    Player leader = Bukkit.getServer().getPlayer(squad.getLeader());
                    if (leader == null) {
                        continue;
                    }

                    message.then(leader.getName()).color(ChatColor.AQUA)
                            .tooltip(getMessageForSquad(squad));
                }
            }

            message.send(player);
        }
    }

    public JSONMessage getMessageForSquad(Squad squad) {
        JSONMessage message2 = JSONMessage.create("");

        message2.then(ChatColor.translateAlternateColorCodes('&', "&aType: &b" + squad.getType()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aMembers: &b" + (squad.getMembers().size()) + "\n"));
        /*message2.then(ChatColor.translateAlternateColorCodes('&', " &aActive Length: &b" + gate.getActiveLength()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aCurrent Length: &b" + gate.getCurrentLength()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aIs Open: &b" + gate.isOpen()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aKeeping Open: &b" + gate.isKeepOpen()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aKeeping Closed: &b" + gate.isKeepClosed()) + "\n");
        message2.then("\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aPressure Plates(&b" + gate.getPressurePlateMap().keySet().size() + "&a):") + "\n");

        int id = 0;

        for (Location location : gate.getPressurePlateMap().keySet()) {
            id++;
            message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aActive: &b" + gate.getPressurePlateMap().get(location) + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
        }

        message2.then("\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aRedstone Blocks(&b" + gate.getRedstoneBlockLocations().size() + "&a):") + "\n");

        id = 0;
        for (Location location : gate.getRedstoneBlockLocations()) {
            id++;
            message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aType: &b" + (location.getBlock() != null ? location.getBlock().getType() : "Null" ) + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
        }

        message2.then("\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aButton(&b" + gate.getButtonLocations().size() + "&a):") + "\n");

        id = 0;
        for (Location location : gate.getButtonLocations()) {
            id++;
            message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aType: &b" + (location.getBlock() != null ? location.getBlock().getType() : "Null" ) + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
        }

        if (getCarbyne().isMythicMobsEnabled()) {
            message2.then("\n");

            int spawnerCount = 0;

            for (MythicSpawner spawner : gate.getMythicSpawners().values()) {
                if (spawner != null) {
                    spawnerCount++;
                }
            }

            message2.then(ChatColor.translateAlternateColorCodes('&', " &aMythic Spawners(&b" + gate.getMythicSpawners().keySet().size() + "&a:&b" + spawnerCount + "&a):") + "\n");

            id = 0;
            for (String spawnerName : gate.getMythicSpawners().keySet()) {
                id++;
                message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aName: &b" + spawnerName + "&a, Null: &b" + (gate.getMythicSpawners().get(spawnerName) == null) + (gate.getMythicSpawners().get(spawnerName) != null ? "&a, Mob Count: &b" + gate.getMythicSpawners().get(spawnerName).getNumberOfMobs() : "")) + "\n");
            }
        }*/

        return message2;
    }
}
