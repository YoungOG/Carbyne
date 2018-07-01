package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoteCommand extends BaseCommand {

    @Command(name = "vote")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        MessageManager.sendMessage(player, "&f[&3Voting&f]: &aYou can vote using the links below.");

        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&7- &dMinecraft-MP&7: "))
                .then("[Link]")
                .color(ChatColor.AQUA)
                .openURL("https://minecraft-mp.com/server/198890/vote/")
                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aThis will take you to &bMinecraft-MP &awhere you can vote."))
                .send(player);
        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&7- &dMinecraftServers&7: "))
                .then("[Link]")
                .color(ChatColor.AQUA)
                .openURL("https://minecraftservers.org/vote/450109")
                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aThis will take you to &bMinecraftServers &awhere you can vote."))
                .send(player);
        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&7- &dPlanetMinecraft&7: "))
                .then("[Link]")
                .color(ChatColor.AQUA)
                .openURL("https://www.planetminecraft.com/server/medieval-lords-4149705/vote/")
                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aThis will take you to &bPlanetMinecraft &awhere you can vote."))
                .send(player);
        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&7- &dMinecraft-Server&7: "))
                .then("[Link]")
                .color(ChatColor.AQUA)
                .openURL("https://minecraft-server.net/vote/MedievalLords/")
                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aThis will take you to &bMinecraft-Server &awhere you can vote."))
                .send(player);
        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&7- &dMinecraft-Server-List&7: "))
                .then("[Link]")
                .color(ChatColor.AQUA)
                .openURL("https://minecraft-server-list.com/server/6163/vote/")
                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aThis will take you to &bMinecraft-Server-List &awhere you can vote."))
                .send(player);
        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&7- &dTopG&7: "))
                .then("[Link]")
                .color(ChatColor.AQUA)
                .openURL("https://topg.org/Minecraft/in-493834")
                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aThis will take you to &bTopG &awhere you can vote."))
                .send(player);
    }
}
