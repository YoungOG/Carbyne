package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateAddRSBCommand extends BaseCommand {

    @Command(name = "gate.addrsb", permission = "carbyne.gate.addrsb", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&c/gate addrsb [name]");
            return;
        }

        String gateId = args[0];

        Gate gate = getGateManager().getGate(gateId);

        if (gate == null) {
            MessageManager.sendMessage(player, "&cCould not find a gate with the ID \"" + gateId + "\".");
            return;
        }

        if (player.getTargetBlock((HashSet<Byte>) null, 50).getType() != Material.REDSTONE_BLOCK) {
            MessageManager.sendMessage(player, "&cYou must be looking at a Redstone Block.");
            return;
        }

        if (gate.getRedstoneBlockLocations().contains(player.getTargetBlock((HashSet<Byte>) null, 50).getLocation())) {
            MessageManager.sendMessage(player, "&cThat Redstone Block is already added to the gate " + gateId + ".");
            return;
        }

        gate.getRedstoneBlockLocations().add(player.getTargetBlock((HashSet<Byte>) null,  50).getLocation());
        gate.saveGate();
        MessageManager.sendMessage(player, "&aYou have added a Redstone Block to the gate &b" + gate.getGateId() + "&a.");
    }
}
