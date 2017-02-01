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
public class GateDelayCommand extends BaseCommand {

    @Command(name = "gate.delay", permission = "carbyne.gate.delay")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(sender, "&c/gate delay [name] [delay]");
            return;
        }

        try {
            String gateId = args[0];
            int delay = Integer.parseInt(args[1]);

            Gate gate = getGateManager().getGate(gateId);

            if (gate == null) {
                MessageManager.sendMessage(sender, "&cCould not find a gate with the ID \"" + gateId + "\".");
                return;
            }

            gate.setDelay(delay);
            gate.saveGate();
            MessageManager.sendMessage(sender, "&aYou have set the delay of the gate &b" + gateId + " &ato &b" + delay + "&a.");
        } catch (NumberFormatException ignored) {
            MessageManager.sendMessage(sender, "&cYou must input a valid number as the delay.");
        }
    }
}
