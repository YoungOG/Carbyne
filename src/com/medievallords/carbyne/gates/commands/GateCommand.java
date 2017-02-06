package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

/**
 * Created by Calvin on 1/30/2017
 * for the Carbyne-Gear project.
 */
public class GateCommand extends BaseCommand {

    //Example command in the class: GateAddRSBCommand

    @Command(name = "gate", permission = "carbyne.gate")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            MessageManager.sendMessage(sender, "&7&m-------&r&7 [ &aGates &7] &m-------");
            MessageManager.sendMessage(sender, "&a/gate create [name] &7- Creates a new gate.");
            MessageManager.sendMessage(sender, "&a/gate active [name] &7- Sets the active length.");
            MessageManager.sendMessage(sender, "&a/gate addPP [name] &7- Adds a PressurePlate to a gate.");
            MessageManager.sendMessage(sender, "&a/gate addRSB [name] &7- Adds a RedstoneBlock to a gate.");
            MessageManager.sendMessage(sender, "&a/gate addB [name] &7- Adds a Button to a gate.");
            MessageManager.sendMessage(sender, "&a/gate addSpawner [name] [spawnerName] &7- Adds a MythicSpawner to a gate.");
            MessageManager.sendMessage(sender, "&a/gate delPP &7- Deletes a PressurePlate from a gate.");
            MessageManager.sendMessage(sender, "&a/gate delRSB &7- Deletes a Redstone Block from a gate.");
            MessageManager.sendMessage(sender, "&a/gate delB &7- Deletes a Button from a gate.");
            MessageManager.sendMessage(sender, "&a/gate delSpawner [name] [SpawnerName] &7- Deletes a MythicSpawner from a gate.");
            MessageManager.sendMessage(sender, "&a/gate list &7- Lists all available gates.");
            MessageManager.sendMessage(sender, "&a/gate status [name] &7- Checks a gates states.");
            MessageManager.sendMessage(sender, "&a/gate rename [name] &7- Renames a gate.");
        }
    }
}
