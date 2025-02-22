package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateListCommand extends BaseCommand {

    @Command(name = "gate.list", permission = "carbyne.gate.list")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length != 0) {
            MessageManager.sendMessage(sender, "&c/gate list");
            return;
        }

        if (StaticClasses.gateManager.getGates().size() <= 0) {
            MessageManager.sendMessage(sender, "&cThere are no available gates to display.");
            return;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            MessageManager.sendMessage(sender, "&aAvailable Gates:");

            JSONMessage message = JSONMessage.create("");

            for (int i = 0; i < StaticClasses.gateManager.getGates().size(); i++) {
                if (i < StaticClasses.gateManager.getGates().size() - 1) {
                    Gate gate = StaticClasses.gateManager.getGates().get(i);



                    message.then(gate.getGateId()).color(ChatColor.AQUA)
                            .tooltip(getMessageForGate(gate))
                            .then(", ").color(ChatColor.GRAY);
                } else {
                    Gate gate = StaticClasses.gateManager.getGates().get(i);

                    message.then(gate.getGateId()).color(ChatColor.AQUA)
                            .tooltip(getMessageForGate(gate));
                }
            }

            message.send(player);
        } else {
            MessageManager.sendMessage(sender, "&aAvailable Gates:");

            List<String> gateIds = new ArrayList<>();
            for (Gate gate : StaticClasses.gateManager.getGates()) {
                gateIds.add("&a" + gate.getGateId());
            }

            MessageManager.sendMessage(sender, gateIds.toString().replace("[", "").replace("]", "").replace(",", ChatColor.GRAY + ","));
        }
    }

    public JSONMessage getMessageForGate(Gate gate) {
        JSONMessage message2 = JSONMessage.create("");

        message2.then(ChatColor.translateAlternateColorCodes('&', "&aGate Id: &b" + gate.getGateId()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aHeartbeat Alive: &b" + (gate.getBukkitTask() != null ? "True" : "False")) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aActive Length: &b" + gate.getActiveLength()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aCurrent Length: &b" + gate.getCurrentLength()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aIs Open: &b" + gate.isOpen()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aKeeping Open: &b" + gate.isKeepOpen()) + "\n");
        message2.then("\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aPressure Plates(&b" + gate.getPressurePlateMap().keySet().size() + "&a):") + "\n");

        int id = 0;

        for (Location location : gate.getPressurePlateMap().keySet()) {
            id++;
            message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aActive: &b" + gate.getPressurePlateMap().get(location) + "&a, CGWorld: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
        }

        message2.then("\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aRedstone Blocks(&b" + gate.getRedstoneBlockLocations().size() + "&a):") + "\n");

        id = 0;
        for (Location location : gate.getRedstoneBlockLocations()) {
            id++;
            message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aType: &b" + (location.getBlock() != null ? location.getBlock().getType() : "Null") + "&a, CGWorld: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
        }

        message2.then("\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aButton(&b" + gate.getButtonLocations().size() + "&a):") + "\n");

        id = 0;
        for (Location location : gate.getButtonLocations()) {
            id++;
            message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aType: &b" + (location.getBlock() != null ? location.getBlock().getType() : "Null") + "&a, CGWorld: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
        }

        if (Carbyne.getInstance().isMythicMobsEnabled()) {
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
        }

        return message2;
    }
}
