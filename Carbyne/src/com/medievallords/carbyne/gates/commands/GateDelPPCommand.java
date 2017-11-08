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
public class GateDelPPCommand extends BaseCommand {

    @Command(name = "gate.delpp", permission = "carbyne.gate.delpp", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&c/gate delpp");
            return;
        }

        if (player.getTargetBlock((HashSet<Byte>) null, 50).getType() != Material.STONE_PLATE) {
            MessageManager.sendMessage(player, "&cYou must be looking at a Pressure Plate.");
            return;
        }

        Gate gate = getGateManager().getGate(player.getTargetBlock((HashSet<Byte>) null, 50).getLocation());

        if (gate == null) {
            MessageManager.sendMessage(player, "&cThere is no gate that is using that Pressure Plate.");
            return;
        }

        gate.getPressurePlateMap().remove(player.getTargetBlock((HashSet<Byte>) null,  50).getLocation());
        gate.saveGate();
        MessageManager.sendMessage(player, "&aYou have deleted a Pressure Plate from the gate &b" + gate.getGateId() + "&a.");
    }
}
