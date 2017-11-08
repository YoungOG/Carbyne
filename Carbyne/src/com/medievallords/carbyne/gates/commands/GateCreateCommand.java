package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateCreateCommand extends BaseCommand {

    @Command(name = "gate.create", permission = "carbyne.gate.create")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&c/gate create [name]");
            return;
        }

        String gateId = args[0];

        if (getGateManager().getGate(gateId) != null) {
            MessageManager.sendMessage(sender, "&cThere is already a with the ID \"" + gateId + "\".");
            return;
        }

        Gate gate = new Gate(gateId);
        gate.setActiveLength(1);
        gate.setCurrentLength(1);
        getGateManager().getGates().add(gate);
        MessageManager.sendMessage(sender, "&aYou have created a new gate named \"&b" + gateId + "&a\".");
    }
}
