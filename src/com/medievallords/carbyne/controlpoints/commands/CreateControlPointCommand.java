package com.medievallords.carbyne.controlpoints.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by Williams on 2017-06-13
 * for the Carbyne project.
 */
public class CreateControlPointCommand extends BaseCommand {

    @Command(name = "controlpoint.create", aliases = {"cp.create", "cp.c", "controlp.c", "controlpoint.c"}, permission = "carbyne.commands.controlpoints.create", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 2) {

            String name = args[0];
            Location location = player.getTargetBlock((Set<Material>) null, 10).getLocation();

            if (location.getBlock() == null || location.getBlock().getType() == Material.AIR) {
                MessageManager.sendMessage(player, "&cYou must be looking at a block. &cUsage: &6/controlpoint create <name> <timer> optional:<displayName>");
                return;
            }

            try {

                getControlPointManager().createControlPoint(player, name, location, Integer.parseInt(args[1]));
                MessageManager.sendMessage(player, "&aControl point &b" + name + " &ahas been created");

            } catch (NumberFormatException e) {
                MessageManager.sendMessage(player, "&cThe timer can only be a number. &cUsage: &6/controlpoint create <name> <timer> optional:<displayName>");
            }

        } else if (args.length == 3) {
            String name = args[0];
            Location location = player.getTargetBlock((Set<Material>) null, 10).getLocation();
            String displayName = args[2];

            if (location.getBlock() == null || location.getBlock().getType() == Material.AIR) {
                MessageManager.sendMessage(player, "&cYou must be looking at a block. &cUsage: &6/controlpoint create <name> <timer> optional:<displayName>");
                return;
            }

            try {

                getControlPointManager().createControlPoint(player, name, location, Integer.parseInt(args[1]), displayName);
                MessageManager.sendMessage(player, "&aControl point &b" + name + " &ahas been created");

            } catch (NumberFormatException e) {
                MessageManager.sendMessage(player, "&cThe timer can only be a number. &cUsage: &6/controlpoint create <name> <timer> optional:<displayName>");
            }

        } else {
            MessageManager.sendMessage(player, "&cUsage: &6/controlpoint create <name> <timer> optional:<displayName>");
        }
    }
}
