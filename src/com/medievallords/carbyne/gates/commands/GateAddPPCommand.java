package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateAddPPCommand extends BaseCommand {

    @Command(name = "gate.addpp", permission = "carbyne.gate.addpp", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&c/gate addpp [name]");
            return;
        }

        String gateId = args[0];

        Gate gate = getGateManager().getGate(gateId);

        if (gate == null) {
            MessageManager.sendMessage(player, "&cCould not find a gate with the ID \"" + gateId + "\".");
            return;
        }

        if (!player.getTargetBlock((HashSet<Byte>) null, 50).getType().toString().contains("PLATE")) {
            MessageManager.sendMessage(player, "&cYou must be looking at a Stone Pressure Plate.");
            return;
        }

        if (gate.getPressurePlateMap().containsKey(player.getTargetBlock((HashSet<Byte>) null, 50).getLocation())) {
            MessageManager.sendMessage(player, "&cThat Pressure Plate is already added to the gate " + gateId + ".");
            return;
        }

        gate.getPressurePlateMap().put(player.getTargetBlock((HashSet<Byte>) null,  50).getLocation(), false);
        MessageManager.sendMessage(player, "&aYou have added a Pressure Plate to the gate &b" + gate.getGateId() + "&a.");
        gate.saveGate();
    }
}
