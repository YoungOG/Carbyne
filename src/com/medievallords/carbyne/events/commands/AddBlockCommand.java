package com.medievallords.carbyne.events.commands;

import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by WE on 2017-06-21.
 *
 */
public class AddBlockCommand extends BaseCommand implements Listener{

    private HashMap<Player, Integer> players = new HashMap<>();

    @Command(name = "hohengrothevent.addblock", aliases = {"he.ab"}, permission = "hohengroth.addwall", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &6/hohengrothevent addblock <wall 0/1/2>");
            return;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            if (players.containsKey(player)) {
                players.remove(player);
                MessageManager.sendMessage(player, "You have been removed");
            } else {
                MessageManager.sendMessage(player, "You are not on the list");
            }

        }

        else if (args[0].equalsIgnoreCase("startbutton")) {
            getCarbyne().getHohengrothSchedule().enterButtonLocation = player.getTargetBlock((Set<Material>) null, 10).getLocation();
            MessageManager.sendMessage(player, "Start button: " + LocationSerialization.serializeLocation(getCarbyne().getHohengrothSchedule().enterButtonLocation));
        }

        else if (args[0].equalsIgnoreCase("startposition")) {
            getCarbyne().getHohengrothSchedule().startPosition = player.getLocation();
            MessageManager.sendMessage(player, "Start position: " + LocationSerialization.serializeLocation(getCarbyne().getHohengrothSchedule().startPosition));
        }

        else if (args[0].equalsIgnoreCase("portalbutton")) {
            getCarbyne().getHohengrothSchedule().enterFirstPortalButtonLocation = player.getTargetBlock((Set<Material>) null, 10).getLocation();
            MessageManager.sendMessage(player, "Portal button: " + LocationSerialization.serializeLocation(getCarbyne().getHohengrothSchedule().enterButtonLocation));
        }

        else if (args[0].equalsIgnoreCase("portalposition")) {
            getCarbyne().getHohengrothSchedule().portalPosition = player.getLocation();
            MessageManager.sendMessage(player, "Portal position: " + LocationSerialization.serializeLocation(getCarbyne().getHohengrothSchedule().portalPosition));
        }


        else {

            try {
                if (Integer.parseInt(args[0]) == 0) {
                    players.put(player, 0);
                    MessageManager.sendMessage(player, "Added for wall 0");

                } else if (Integer.parseInt(args[0]) == 1) {
                    players.put(player, 1);
                    MessageManager.sendMessage(player, "Added for wall 1");

                } else if (Integer.parseInt(args[0]) == 2) {
                    players.put(player, 2);
                    MessageManager.sendMessage(player, "Added for wall 2");

                } else {
                    MessageManager.sendMessage(player, "There are only 3 walls, 0, 1 or 2. &cUsage: &6/hohengrothevent addblock <wall>");
                }

            } catch (NumberFormatException e) {
                MessageManager.sendMessage(player, "The wall can only be a number. &cUsage: &6/hohengrothevent addblock <wall 0/1/2>");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (players.containsKey(event.getPlayer())) {
            int i = players.get(event.getPlayer());

            String location = LocationSerialization.serializeLocation(event.getBlock().getLocation());
            if (i == 0) {
                if (!getCarbyne().getHohengrothSchedule().startWall.contains(location)) {
                    getCarbyne().getHohengrothSchedule().startWall.add(location);
                    MessageManager.sendMessage(event.getPlayer(), "Block added for wall start at: " + location);
                } else {
                    MessageManager.sendMessage(event.getPlayer(), "Block is already set at: " + location);
                }


            } else if (i == 1) {

                if (!getCarbyne().getHohengrothSchedule().wallOne.contains(location)) {
                    getCarbyne().getHohengrothSchedule().wallOne.add(location);
                    MessageManager.sendMessage(event.getPlayer(), "Block added for wall one at: " + location);
                } else {
                    MessageManager.sendMessage(event.getPlayer(), "Block is already set at: " + location);
                }

            } else if (i == 2) {

                if (!getCarbyne().getHohengrothSchedule().wallTwo.contains(location)) {
                    getCarbyne().getHohengrothSchedule().wallTwo.add(location);
                    MessageManager.sendMessage(event.getPlayer(), "Block added for wall two at: " + location);
                } else {
                    MessageManager.sendMessage(event.getPlayer(), "Block is already set at: " + location);
                }
            }
        }
    }

}
