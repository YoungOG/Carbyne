package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateAddSpawnerCommand extends BaseCommand {

    @Command(name = "gate.addspawner", permission = "carbyne.gate.addspawner", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&c/gate addspawner [gateName] [spawnerName]");
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

        MythicSpawner mythicSpawner = null;

        for (MythicSpawner spawners : MythicMobs.inst().getSpawnerManager().listSpawners) {
            if (spawners.getInternalName().equalsIgnoreCase(args[1])) {
                mythicSpawner = spawners;
            }
        }

        if (mythicSpawner == null) {
            MessageManager.sendMessage(player, "&cCould not find a MythicSpawner with the name \"" + args[1] + "\".");
            return;
        }

        if (gate.getMythicSpawners().containsKey(mythicSpawner.getInternalName())) {
            MessageManager.sendMessage(player, "&cThat MythicSpawner is already added to this gate.");
            return;
        }

        gate.getMythicSpawners().put(mythicSpawner.getInternalName(), mythicSpawner);
        gate.closeGate();
        gate.saveGate();
        MessageManager.sendMessage(player, "&aYou have added the MythicSpawner &b" + mythicSpawner.getInternalName() + " &ato the gate &b" + gate.getGateId() + "&a.");
    }
}
