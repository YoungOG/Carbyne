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
public class GateRenameCommand extends BaseCommand {

    @Command(name = "gate.rename", permission = "carbyne.gate.rename")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(sender, "&c/gate rename [oldName] [newName]");
            return;
        }

        String currentGateId = args[0];
        String newGateId = args[1];

        if (getGateManager().getGate(currentGateId) == null) {
            MessageManager.sendMessage(sender, "&cCould not find a gate with the ID \"" + currentGateId + "\".");
            return;
        }

        Gate gate = getGateManager().getGate(currentGateId);

        if (getGateManager().getGate(newGateId) != null) {
            MessageManager.sendMessage(sender, "&cThere is already a with the ID \"" + newGateId + "\".");
            return;
        }

        currentGateId = gate.getGateId();
        gate.setGateId(newGateId);

        getCarbyne().getGateFileConfiguration().getConfigurationSection("Gates").set(currentGateId, newGateId);

        gate.saveGate();

        MessageManager.sendMessage(sender, "&aYou have renamed the gate \"&b" + currentGateId + "&a\" to \"&b" + newGateId + "&a\".");
    }
}
