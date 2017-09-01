package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Williams on 2017-08-08
 * for the Carbyne project.
 */
public class IgnoreCommand extends BaseCommand implements Listener {

    @Command(name = "ignore", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/ignore <player>");
            return;
        }

        Player toIgnore = Bukkit.getServer().getPlayer(args[0]);
        if (toIgnore == null) {
            MessageManager.sendMessage(player, "&cCould not find that player");
            return;
        }

        if (getProfileManager().getProfile(player.getUniqueId()) == null) {
            MessageManager.sendMessage(player, "&cThere was an error");
            return;
        }

        if (getProfileManager().getProfile(player.getUniqueId()).getIgnoredPlayers().contains(toIgnore.getUniqueId())) {
            getProfileManager().getProfile(player.getUniqueId()).getIgnoredPlayers().remove(toIgnore.getUniqueId());
            MessageManager.sendMessage(player, "&6You are no longer ignoring &b" + toIgnore.getName());
        } else {
            getProfileManager().getProfile(player.getUniqueId()).getIgnoredPlayers().add(toIgnore.getUniqueId());
            MessageManager.sendMessage(player, "&6You are now ignoring &b" + toIgnore.getName());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage();
        if (isMessage(cmd)) {
            String[] split = cmd.split(" ");
            if (split.length >= 2) {
                Player player = Bukkit.getServer().getPlayer(split[1]);
                if (player == null) {
                    return;
                }

                Profile profile = getProfileManager().getProfile(player.getUniqueId());
                if (profile.getIgnoredPlayers().contains(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    MessageManager.sendMessage(event.getPlayer(), "&cThat player is ignoring you");
                }
            }
        } else if (isMail(cmd)) {
            String[] split = cmd.split(" ");
            if (split.length > 2) {
                if (!split[1].equalsIgnoreCase("send")) {
                    return;
                }

                Player player = Bukkit.getServer().getPlayer(split[2]);
                if (player == null) {
                    return;
                }

                Profile profile = getProfileManager().getProfile(player.getUniqueId());
                if (profile.getIgnoredPlayers().contains(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    MessageManager.sendMessage(event.getPlayer(), "&cThat player is ignoring you");
                }
            }
        }
    }

    private boolean isMessage(String msg) {
        return msg.toLowerCase().startsWith("/msg") || msg.toLowerCase().startsWith("/m") || msg.toLowerCase().startsWith("/whisper") || msg.toLowerCase().startsWith("/tell");

    }

    private boolean isMail(String msg) {
        return msg.toLowerCase().startsWith("/mail") || msg.toLowerCase().startsWith("/email") || msg.toLowerCase().startsWith("/essentials:mail") || msg.toLowerCase().startsWith("/essentials:email");

    }
}
