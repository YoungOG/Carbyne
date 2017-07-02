package com.medievallords.carbyne.utils;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;

public class MessageManager {

    private static Map<String, String> placeholders;

    static {
        (placeholders = Maps.newHashMap()).put("<3", "\u2764");
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMessage(UUID uniqueId, String message) {
        if (Bukkit.getPlayer(uniqueId) != null) {
            Bukkit.getPlayer(uniqueId).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static void sendMessage(Location center, int radius, String message) {
        for (Player player : PlayerUtility.getPlayersInRadius(center, radius)) {
            MessageManager.sendMessage(player, message);
        }
    }

    public static void sendMessage(CommandSender sender, String[] messages) {
        for (String s : messages) {
            sendMessage(sender, s);
        }
    }

    public static void sendMessage(Player player, String[] messages) {
        for (String s : messages) {
            sendMessage(player, s);
        }
    }

    public static void sendMessage(UUID uniqueId, String[] messages) {
        for (String s : messages) {
            if (Bukkit.getPlayer(uniqueId) != null) {
                sendMessage(uniqueId, s);
            }
        }
    }

    public static void broadcastMessage(String message) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void broadcastMessage(String message, String permission) {
        Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', message), permission);
    }

    public static String format(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String formatted = formatter.format(amount);
        if(formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted;
    }

    public static String replaceSymbols(String input) {
        if (input == null || input.length() == 0) {
            return input;
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            input = input.replace(entry.getKey(), entry.getValue());
        }

        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
