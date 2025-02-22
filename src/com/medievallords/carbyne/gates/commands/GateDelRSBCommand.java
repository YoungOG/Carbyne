package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateDelRSBCommand extends BaseCommand {

    @Command(name = "gate.delrsb", permission = "carbyne.gate.delrsb", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&c/gate delrsb");
            return;
        }

        if (player.getTargetBlock(null, 50).getType() != Material.REDSTONE_BLOCK) {
            MessageManager.sendMessage(player, "&cYou must be looking at a Redstone Block.");
            return;
        }

        Gate gate = StaticClasses.gateManager.getGate(player.getTargetBlock(null, 50).getLocation());

        if (gate == null) {
            MessageManager.sendMessage(player, "&cThere is no gate that is using that Redstone Block.");
            return;
        }

        gate.getRedstoneBlockLocations().remove(player.getTargetBlock(null, 50).getLocation());
        gate.saveGate();
        MessageManager.sendMessage(player, "&aYou have deleted a Redstone Block from the gate &b" + gate.getGateId() + "&a.");
    }
}
