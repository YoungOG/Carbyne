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
            MessageManager.sendMessage(sender, "&a/gate delay [name] &7- Sets a gates delay.");
            MessageManager.sendMessage(sender, "&a/gate addPP [name] &7- Adds a PressurePlate to a gate.");
            MessageManager.sendMessage(sender, "&a/gate addRSB [name] &7- Adds a RedstoneBlock to a gate.");
            MessageManager.sendMessage(sender, "&a/gate addB [name] &7- Adds a button to a gate.");
            MessageManager.sendMessage(sender, "&a/gate status [name] &7- Checks a gates states.");
            MessageManager.sendMessage(sender, "&a/gate reset [name] &7- Resets a gate.");
            MessageManager.sendMessage(sender, "&a/gate rename [name] &7- Renames a gate.");
        }
    }
}
