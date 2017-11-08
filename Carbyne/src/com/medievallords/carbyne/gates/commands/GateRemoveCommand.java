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
public class GateRemoveCommand extends BaseCommand {

    @Command(name = "gate.remove", permission = "carbyne.gate.remove")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "&c/gate remove [name]");
            return;
        }

        String gateId = args[0];

        if (getGateManager().getGate(gateId) == null) {
            MessageManager.sendMessage(sender, "&cCould not find a gate with the ID \"" + gateId + "\".");
            return;
        }

        Gate gate = getGateManager().getGate(gateId);
        getGateManager().getGates().remove(gate);
        getCarbyne().getGateFileConfiguration().getConfigurationSection("Gates").set(gate.getGateId(), null);

        MessageManager.sendMessage(sender, "&aYou have removed a gate named \"&b" + gateId + "&a\".");
    }
}
