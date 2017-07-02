package com.medievallords.carbyne.staff.commands;

import com.google.common.primitives.Ints;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class SetSlotsCommand extends BaseCommand implements Listener {

    @Command(name = "setslots", permission = "carbyne.staff.setslots")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length == 0) {
            MessageManager.sendMessage(sender, "&cUsage: /setslots <number>");
            return;
        }

        if (args.length == 1) {
            if (Ints.tryParse(args[0]) == null) {
                MessageManager.sendMessage(sender, "&cYou must input a valid number.");
                return;
            }

            getStaffManager().setServerSlots(Integer.parseInt(args[0]));
            MessageManager.sendMessage(sender, "&aThe server slots have been set to &b" + args[0] + "&a.");
        }
    }

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        if (getStaffManager().getServerSlots() > 0) {
            event.setMaxPlayers(getStaffManager().getServerSlots());
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (getStaffManager().getServerSlots() > 0 && PlayerUtility.getOnlinePlayers().size() >= getStaffManager().getServerSlots()) {
            if (!player.hasPermission("carbyne.bypassfull") && !player.hasPermission("carbyne.staff")) {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, ChatColor.translateAlternateColorCodes('&', "&5Medieval Lords &cis full! \n&cPurchase a rank at &6playminecraft.buycraft.net &cto join."));
            } else {
                event.allow();
            }
        }
    }
}
