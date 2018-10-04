package com.medievallords.carbyne.squads.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
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
public class SquadChatCommand extends BaseCommand implements Listener {

    private ArrayList<UUID> partyChatters = new ArrayList<>();

    public SquadChatCommand() {
        Bukkit.getPluginManager().registerEvents(this, Carbyne.getInstance());
    }

    @Command(name = "squad.chat", inGameOnly = true, aliases = {"squad.ch", "party.ch", "p.chat", "p.ch", "p.c", "s.chat", "s.ch"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Squad squad = StaticClasses.squadManager.getSquad(player.getUniqueId());

        if (squad == null) {
            MessageManager.sendMessage(player, "&cYou are not in a squad.");
            return;
        }

        if (args.length == 0) {
            if (!partyChatters.contains(player.getUniqueId())) {
                partyChatters.add(player.getUniqueId());

                MessageManager.sendMessage(player, "&aYou have entered the squad chat.");
            } else {
                partyChatters.remove(player.getUniqueId());

                MessageManager.sendMessage(player, "&aYou have left the squad chat.");
            }
        } else {
            String message = StringUtils.join(args, ' ', 0, args.length);

            System.out.println("[Party Message] " + player.getName() + ": " + message);
            squad.sendAllMembersMessage(ChatColor.translateAlternateColorCodes('&', "&b" + player.getName() + ": ") + ChatColor.AQUA + message);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (partyChatters.contains(event.getPlayer().getUniqueId())) {
            Squad squad = StaticClasses.squadManager.getSquad(event.getPlayer().getUniqueId());

            if (squad == null) {
                partyChatters.remove(event.getPlayer().getUniqueId());
                return;
            }

            event.setCancelled(true);

            squad.sendAllMembersMessage(ChatColor.translateAlternateColorCodes('&', "&b" + event.getPlayer().getName() + ": ") + ChatColor.AQUA + event.getMessage());
            System.out.println("[Party Message] " + event.getPlayer().getName() + ": " + event.getMessage());
        }
    }
}
