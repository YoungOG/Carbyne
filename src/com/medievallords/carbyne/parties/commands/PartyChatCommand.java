package com.medievallords.carbyne.parties.commands;

import com.medievallords.carbyne.parties.Party;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Williams on 2017-03-13.
 * for the Carbyne project.
 */
public class PartyChatCommand extends BaseCommand implements Listener {

    private ArrayList<UUID> partyChatters = new ArrayList<>();

    public PartyChatCommand() {
        Bukkit.getPluginManager().registerEvents(this, getCarbyne());
    }

    @Command(name = "party.chat", inGameOnly = true, aliases = {"ch"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Party party = getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            MessageManager.sendMessage(player, "&cYou are not in a party.");
            return;
        }

        if (args.length == 0) {
            if (!partyChatters.contains(player.getUniqueId())) {
                partyChatters.add(player.getUniqueId());

                MessageManager.sendMessage(player, "&aYou have entered the party chat.");
            } else {
                partyChatters.remove(player.getUniqueId());

                MessageManager.sendMessage(player, "&aYou have left the party chat.");
            }
        } else if (args.length > 0) {
            String message = StringUtils.join(args, ' ', 0, args.length);

            party.sendAllMembersMessage(ChatColor.translateAlternateColorCodes('&', "&b" + player.getName() + ": ") + ChatColor.AQUA + message);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (partyChatters.contains(event.getPlayer().getUniqueId())) {
            Party party = getPartyManager().getParty(event.getPlayer().getUniqueId());

            if (party == null) {
                partyChatters.remove(event.getPlayer().getUniqueId());
                return;
            }

            event.setCancelled(true);

            party.sendAllMembersMessage(ChatColor.translateAlternateColorCodes('&', "&b" + event.getPlayer().getName() + ": ") + ChatColor.AQUA + event.getMessage());
        }
    }
}
