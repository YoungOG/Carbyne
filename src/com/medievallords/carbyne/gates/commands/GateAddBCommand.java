package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateAddBCommand extends BaseCommand {

    @Command(name = "gate.addb", permission = "carbyne.gate.addb", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&c/gate addb [name]");
            return;
        }

        String gateId = args[0];

        Gate gate = StaticClasses.gateManager.getGate(gateId);

        if (gate == null) {
            MessageManager.sendMessage(player, "&cCould not find a gate with the ID \"" + gateId + "\".");
            return;
        }

        if (!player.getTargetBlock(null, 50).getType().toString().contains("BUTTON")) {
            MessageManager.sendMessage(player, "&cYou must be looking at a Stone Button.");
            return;
        }

        if (gate.getButtonLocations().contains(player.getTargetBlock(null, 50).getLocation())) {
            MessageManager.sendMessage(player, "&cThat Button is already added to the gate " + gateId + ".");
            return;
        }

        gate.getButtonLocations().add(player.getTargetBlock(null, 50).getLocation());
        gate.saveGate();
        MessageManager.sendMessage(player, "&aYou have added a Button to the gate &b" + gate.getGateId() + "&a.");
    }
}
