package com.medievallords.carbyne.utils;

import com.google.common.collect.Maps;
import com.medievallords.carbyne.Carbyne;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

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

    public static void sendStaffMessage(CommandSender sender, String message) {
        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (all.hasPermission("carbyne.staff")) {
                all.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d" + sender.getName() + ": ") + message);
            }
        }

        Carbyne.getInstance().getLogger().log(Level.INFO, "[Staff Message]: " + sender.getName() + ": " + message);
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

    public static String getPotionAmplifierInRomanNumerals(int amplifier) {
        switch (amplifier) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VII";
            case 9:
                return "IX";
            case 10:
                return "X";

            default:
                return amplifier + "";
        }
    }

    public static String getPotionTypeFriendlyName(PotionEffectType potionEffectType) {
        switch (potionEffectType.getName()) {
            case "ABSORPTION":
                return "Absorption";
            case "BLINDNESS":
                return "Blindness";
            case "CONFUSION":
                return "Nausea";
            case "DAMAGE_RESISTANCE":
                return "Damage Resistance";
            case "FAST_DIGGING":
                return "Haste";
            case "HARM":
                return "Instant Harm";
            case "FIRE_RESISTANCE":
                return "Fire Resistance";
            case "HEAL":
                return "Instant Heal";
            case "HEALTH_BOOST":
                return "Health Boost";
            case "HUNGER":
                return "Hunger";
            case "INCREASE_DAMAGE":
                return "Strength";
            case "INVISIBILITY":
                return "Invisibility";
            case "JUMP":
                return "Jump";
            case "NIGHT_VISION":
                return "Night Vision";
            case "POISON":
                return "Poison";
            case "REGENERATION":
                return "Regeneration";
            case "SATURATION":
                return "Saturation";
            case "SLOW":
                return "Slowness";
            case "SLOW_DIGGING":
                return "Fatigue";
            case "SPEED":
                return "Speed";
            case "WATER_BREATHING":
                return "Water Breathing";
            case "WEAKNESS":
                return "Weakness";
            case "WITHER":
                return "Wither";
        }

        return null;
    }

    public static String getEnchantmentFriendlyName(Enchantment enchantment) {
        switch (enchantment.getName()) {
            case "ARROW_DAMAGE":
                return "Power";
            case "ARROW_FIRE":
                return "Flame";
            case "ARROW_KNOCKBACK":
                return "Punch";
            case "ARROW_INFINITE":
                return "Infinity";
            case "DAMAGE_ALL":
                return "Sharpness";
            case "KNOCKBACK":
                return "Knockback";
            case "DAMAGE_ARTHROPODS":
                return "Bane of Arthropods";
            case "DAMAGE_UNDEAD":
                return "Smite";
            case "DIG_SPEED":
                return "Efficiency";
            case "DURABILITY":
                return "Unbreaking";
            case "FIRE_ASPECT":
                return "Fire Aspect";
            case "LOOT_BONUS_BLOCKS":
                return "Fortune";
            case "LOOT_BONUS_MOBS":
                return "Looting";
            case "OXYGEN":
                return "Respiration";
            case "PROTECTION_ENVIRONMENTAL":
                return "Protection";
            case "PROTECTION_FALL":
                return "Feather Falling";
            case "PROTECTION_FIRE":
                return "Fire Protection";
            case "PROTECTION_PROJECTILE":
                return "Projectile Protection";
            case "PROTECTION_EXPLOSIONS":
                return "Blast Protection";
            case "SILK_TOUCH":
                return "Silk Touch";
            case "THORNS":
                return "Thorns";
            case "DEPTH_STRIDER":
                return "Depth Strider";
        }
        return "";

    }

    public static String convertSecondsToMinutes(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        String disMinu = "" + minutes;
        String disSec = (seconds < 10 ? "0" : "") + seconds;
        return disMinu + ":" + disSec;
    }

    public static String stripStringOfAmpersandColors(String string) {
        if (string == null) return "";
        char[] chars = string.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            if (current == '&') i++;
            else sb.append(current);
        }
        return sb.toString();
    }
}
