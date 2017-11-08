package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DiscordCommand extends BaseCommand {

    @Command(name = "discord")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&7You can join our Discord server using the &b&l["))
                .then("Invitation")
                .color(ChatColor.AQUA)
                .style(ChatColor.BOLD)
                .style(ChatColor.UNDERLINE)
                .openURL("https://discord.gg/t6vvffu")
                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aThis will invite you to join our discord."))
                .then(ChatColor.translateAlternateColorCodes('&', "&r&b&l] &alink."))
                .send(player);
    }
}