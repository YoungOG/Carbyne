package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-08-06.
 */
public class WebsiteCommand extends BaseCommand {

    @Command(name = "website", aliases = {"webpage", "web"})
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&7You can visit our website using the &b&l["))
                .then("Website Link")
                .color(ChatColor.AQUA)
                .style(ChatColor.BOLD)
                .style(ChatColor.UNDERLINE)
                .openURL("http://www.playminecraft.org/")
                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aThis will take you to our website!"))
                .then(ChatColor.translateAlternateColorCodes('&', "&r&b&l]."))
                .send(player);
    }
}
