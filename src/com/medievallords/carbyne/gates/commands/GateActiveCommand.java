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
public class GateActiveCommand extends BaseCommand {

    @Command(name = "gate.active", permission = "carbyne.gate.active")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(sender, "&c/gate active [name] [length]");
            return;
        }

        try {
            String gateId = args[0];
            int length = Integer.parseInt(args[1]);

            Gate gate = getGateManager().getGate(gateId);

            if (gate == null) {
                MessageManager.sendMessage(sender, "&cCould not find a gate with the ID \"" + gateId + "\".");
                return;
            }

            gate.setActiveLength(length);
            gate.saveGate();
            MessageManager.sendMessage(sender, "&aYou have set the active length of the gate &b" + gateId + " &ato &b" + length + "&a.");
        } catch (NumberFormatException ignored) {
            MessageManager.sendMessage(sender, "&cYou must input a valid number as the length.");
        }
    }
}
