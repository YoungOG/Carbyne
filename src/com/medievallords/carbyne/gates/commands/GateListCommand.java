package com.medievallords.carbyne.gates.commands;

import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Calvin on 1/31/2017
 * for the Carbyne-Gear project.
 */
public class GateListCommand extends BaseCommand {

    @Command(name = "gate.list", permission = "carbyne.gate.list")
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length != 0) {
            MessageManager.sendMessage(sender, "&c/gate list");
            return;
        }

        if (getGateManager().getGates().size() <= 0) {
            MessageManager.sendMessage(sender, "&cThere are no available gates to display.");
            return;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            MessageManager.sendMessage(sender, "&aAvailable Gates:");

            JSONMessage message1 = JSONMessage.create("");

            for (int i = 0; i < getGateManager().getGates().size(); i++) {
                if (i < getGateManager().getGates().size() - 1) {
                    Gate gate = getGateManager().getGates().get(i);

                    message1.then(gate.getGateId()).color(ChatColor.AQUA)
                            .tooltip(ChatColor.translateAlternateColorCodes('&', "&aDelay: &b" + gate.getDelay()))
                            .then(", ").color(ChatColor.GRAY);
                } else {
                    Gate gate = getGateManager().getGates().get(i);

                    message1.then(gate.getGateId()).color(ChatColor.AQUA)
                            .tooltip(ChatColor.translateAlternateColorCodes('&', "&aDelay: &b" + gate.getDelay()));
                }
            }

            message1.send(player);

//            FancyMessage message = new FancyMessage("");
//            for (int i = 0; i < getCrateManager().getCrates().size(); i++) {
//                if (i < getCrateManager().getCrates().size() - 1) {
//                    Crate crate = getCrateManager().getCrates().get(i);
//
//                    message.then(crate.getName()).color(ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_NAME_COLOR.toString()))
//                            .tooltip(ChatColor.translateAlternateColorCodes('&', "&aLocation: " + (crate.getLocation() != null ? "World: &b" + crate.getLocation().getWorld().getName() + "&a, X: &b" + crate.getLocation().getBlockX() + "&a, Y: &b" + crate.getLocation().getBlockY() + "&a, Z: &b" + crate.getLocation().getBlockZ() + "&a)" : "&cNot set") + "\n&aRewards Amount: &b" + crate.getRewardsAmount()))
//                            .then(", ").color(ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_COMMA_COLOR.toString()));
//                } else {
//                    Crate crate = getCrateManager().getCrates().get(i);
//
//                    message.then(crate.getName()).color(ChatColor.valueOf(Lang.SUCCESS_CRATE_LIST_NAME_COLOR.toString()))
//                            .tooltip(ChatColor.translateAlternateColorCodes('&', "&aLocation: " + (crate.getLocation() != null ? "World: &b" + crate.getLocation().getWorld().getName() + "&a, X: &b" + crate.getLocation().getBlockX() + "&a, Y: &b" + crate.getLocation().getBlockY() + "&a, Z: &b" + crate.getLocation().getBlockZ() + "&a)" : "&cNot set") + "\n&aRewards Amount: &b" + crate.getRewardsAmount()));
//                }
//            }
//            message.send(player);
        } else {
            MessageManager.sendMessage(sender, "&aAvailable Gates:");

            List<String> gateIds = new ArrayList<>();
            for (Gate gate : getGateManager().getGates()) {
                gateIds.add("&a" + gate.getGateId());
            }

            MessageManager.sendMessage(sender, gateIds.toString().replace("[", "").replace("]", "").replace(",", ChatColor.GRAY + ","));
        }
    }
}
