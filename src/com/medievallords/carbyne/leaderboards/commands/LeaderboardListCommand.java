package com.medievallords.carbyne.leaderboards.commands;

import com.medievallords.carbyne.leaderboards.Leaderboard;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Calvin on 1/23/2017
 * for the Carbyne-Gear project.
 */
public class LeaderboardListCommand extends BaseCommand {

    @Command(name = "leaderboard.list", permission = "carbyne.command.leaderboard")
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();
        CommandSender sender = command.getSender();

        if (getLeaderboardManager().getLeaderboards().size() <= 0) {
            MessageManager.sendMessage(sender, "&cThere are no available leaderboards to display.");
            return;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            MessageManager.sendMessage(sender, "&aAvailable Leaderboards:");

            JSONMessage message = JSONMessage.create("");

            for (int i = 0; i < getLeaderboardManager().getLeaderboards().size(); i++) {
                if (i < getLeaderboardManager().getLeaderboards().size() - 1) {
                    Leaderboard leaderboard = getLeaderboardManager().getLeaderboards().get(i);



                    message.then(leaderboard.getBoardId()).color(ChatColor.AQUA)
                            .tooltip(getMessageForLeaderboard(leaderboard))
                            .then(", ").color(ChatColor.GRAY);
                } else {
                    Leaderboard leaderboard = getLeaderboardManager().getLeaderboards().get(i);

                    message.then(leaderboard.getBoardId()).color(ChatColor.AQUA)
                            .tooltip(getMessageForLeaderboard(leaderboard));
                }
            }

            message.send(player);
        } else {
            MessageManager.sendMessage(sender, "&aAvailable Leaderboards:");

            List<String> boardIds = new ArrayList<>();
            for (Leaderboard leaderboard : getLeaderboardManager().getLeaderboards()) {
                boardIds.add("&a" + leaderboard.getBoardId());
            }

            MessageManager.sendMessage(sender, boardIds.toString().replace("[", "").replace("]", "").replace(",", ChatColor.GRAY + ","));
        }
    }

    public JSONMessage getMessageForLeaderboard(Leaderboard leaderboard) {
        JSONMessage message2 = JSONMessage.create("");

        message2.then(ChatColor.translateAlternateColorCodes('&', "&aBoard Id: &b" + leaderboard.getBoardId()) + "\n");
        message2.then("\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aSign Locations(&b" + leaderboard.getSignLocations().size() + "&a):") + "\n");

        int id = 0;
        for (Location location : leaderboard.getSignLocations()) {
            id++;
            message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aWorld: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
        }

        message2.then("\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aHead Locations(&b" + leaderboard.getHeadLocations().size() + "&a):") + "\n");

        id = 0;
        for (Location location : leaderboard.getHeadLocations()) {
            id++;
            message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aType: &b" + (location.getBlock() != null ? location.getBlock().getType() : "Null" ) + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
        }

        return message2;
    }
}
