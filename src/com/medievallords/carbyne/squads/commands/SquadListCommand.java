package com.medievallords.carbyne.squads.commands;

import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.squads.SquadType;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Williams on 2017-03-12
 * for the Carbyne project.
 */
public class SquadListCommand extends BaseCommand {

    @Command(name = "squad.list", aliases = {"i"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (getSquadManager().getSquads().size() <= 0) {
            MessageManager.sendMessage(sender, "&cThere are no available squads to display.");
            return;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            int page = 1;

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("list")) {
                    if (NumberUtils.isNumber(args[1])) {
                        page = (int) Double.parseDouble(args[1]);
                    }
                }
            }

            int listSize = Math.round(getSquadManager().getSquads().size() / 10);
            if (listSize == 0) {
                listSize = 1;
            }

            if (page > listSize) {
                page = listSize;
            }

            MessageManager.sendMessage(player, "&aSquad List &7[Page " + page + "/" + listSize + "]");

            for (int i = page * 10 - 10; i < page * 10; i++) {
                if (getSquadManager().getSquads().size() > i) {
                    Squad squad = getSquadManager().getSquads().get(i);

                    JSONMessage message = JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&7" + (i + 1) + ". &a" + Bukkit.getPlayer(squad.getLeader()).getName() + "'s Squad &7[&b" + squad.getAllPlayers().size() + "&7/5] [" + (squad.getType() == SquadType.PUBLIC ? "&bPublic" : "&cPrivate") + "&7]")).tooltip(getMessageForSquad(squad)).runCommand("/squad info " + squad.getLeader());
                    message.send(player);
                }
            }

            MessageManager.sendMessage(player, "&7You are currently on Page " + page + "/" + listSize + ".");
            MessageManager.sendMessage(player, "&7To view other pages use &a/squad list <page>");
        }
    }

    public JSONMessage getMessageForSquad(Squad squad) {
        JSONMessage message2 = JSONMessage.create("");

        message2.then(ChatColor.translateAlternateColorCodes('&', "&aType&7: &b" + squad.getType().toString().toLowerCase().substring(0, 1).toUpperCase() + squad.getType().toString().toLowerCase().substring(1)) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', "&aMembers &7(&b" + squad.getAllPlayers().size() + "&7):\n"));

        List<String> memberNames = new ArrayList<>();
        for (UUID uuid : squad.getAllPlayers()) {
            memberNames.add(Bukkit.getPlayer(uuid).getName());
        }

        message2.then(ChatColor.translateAlternateColorCodes('&', "&b" + memberNames.toString().replace("[", "").replace("]", "").replace(",", "&7,&b")));

        return message2;
    }
}
