package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateDelSpawnerCommand extends BaseCommand {

    @Command(name = "gate.delspawner", permission = "carbyne.gate.delspawner", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&c/gate delspawner [gateName] [spawnerName]");
            return;
        }

        if (!getCarbyne().isMythicMobsEnabled()) {
            MessageManager.sendMessage(player, "&cMythicMobs is not enabled.");
            return;
        }

        Gate gate = getGateManager().getGate(args[0]);

        if (gate == null) {
            MessageManager.sendMessage(player, "&cCould not find a gate with the ID \"" + args[0] + "\".");
            return;
        }

        String name = "";
        for (String spawnerName : gate.getMythicSpawners().keySet()) {
            if (spawnerName.equalsIgnoreCase(args[1])) {
                name = spawnerName;
            }
        }

        if (!name.equalsIgnoreCase(args[1])) {
            MessageManager.sendMessage(player, "&cCould not find a MythicSpawner with the name \"" + args[1] + "\" added to the gate \"" + gate.getGateId() + "\".");
            return;
        }

        gate.getMythicSpawners().remove(name);
        gate.closeGate();
        gate.saveGate();
        MessageManager.sendMessage(player, "&aYou have removed the MythicSpawner &b" + name + " &afrom the gate &b" + gate.getGateId() + "&a.");
    }
}
