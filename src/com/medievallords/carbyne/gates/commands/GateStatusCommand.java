package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateStatusCommand extends BaseCommand {

    @Command(name = "gate.status", permission = "carbyne.gate.status")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&c/gate status [name]");
            return;
        }

        String gateId = args[0];

        if (getGateManager().getGate(gateId) == null) {
            MessageManager.sendMessage(sender, "&cCould not find a gate with the ID \"" + gateId + "\".");
            return;
        }

        Gate gate = getGateManager().getGate(gateId);

        MessageManager.sendMessage(sender, "&aGate Id: &b" + gate.getGateId());
        MessageManager.sendMessage(sender, " &aHeartbeat Alive: &b" + (gate.getHeartbeat() != null ? gate.getHeartbeat().isAlive() : "False"));
        MessageManager.sendMessage(sender, " &aActive Length: &b" + gate.getActiveLength());
        MessageManager.sendMessage(sender, " &aCurrent Length: &b" + gate.getCurrentLength());
        MessageManager.sendMessage(sender, " &aIs Open: &b" + gate.isOpen());
        MessageManager.sendMessage(sender, " &aKeeping Open: &b" + gate.isKeepOpen());
        MessageManager.sendMessage(sender, " &aKeeping Closed: &b" + gate.isKeepClosed());

        MessageManager.sendMessage(sender, " &aPressure Plates(&b" + gate.getPressurePlateMap().keySet().size() + "&a):");

        int id = 0;
        for (Location location : gate.getPressurePlateMap().keySet()) {
            id++;
            MessageManager.sendMessage(sender, "   &b" + id + "&7. &aActive: &b" + gate.getPressurePlateMap().get(location) + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ());
        }

        MessageManager.sendMessage(sender, " &aRedstone Blocks(&b" + gate.getRedstoneBlockLocations().size() + "&a):");

        id = 0;
        for (Location location : gate.getRedstoneBlockLocations()) {
            id++;
            MessageManager.sendMessage(sender, "   &b" + id + "&7. &aType: &b" + (location.getBlock() != null ? location.getBlock().getType() : "Null") + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ());
        }

        MessageManager.sendMessage(sender, " &aButton(&b" + gate.getButtonLocations().size() + "&a):");

        id = 0;
        for (Location location : gate.getButtonLocations()) {
            id++;
            MessageManager.sendMessage(sender, "   &b" + id + "&7. &aType: &b" + (location.getBlock() != null ? location.getBlock().getType() : "Null") + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ());
        }

        if (getCarbyne().isMythicMobsEnabled()) {
            int spawnerCount = 0;

            for (MythicSpawner spawner : gate.getMythicSpawners().values()) {
                if (spawner != null) {
                    spawnerCount++;
                }
            }

            MessageManager.sendMessage(sender, " &aMythic Spawners(&b" + gate.getMythicSpawners().keySet().size() + "&a:&b" + spawnerCount + "&a):");

            id = 0;
            for (String spawnerName : gate.getMythicSpawners().keySet()) {
                id++;
                MessageManager.sendMessage(sender, "   &b" + id + "&7. &aName: &b" + spawnerName + "&a, Null: &b" + (gate.getMythicSpawners().get(spawnerName) == null) + (gate.getMythicSpawners().get(spawnerName) != null ? "&a, Mob Count: &b" + gate.getMythicSpawners().get(spawnerName).getNumberOfMobs() : ""));
            }
        }
    }
}
