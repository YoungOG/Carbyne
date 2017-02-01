package com.medievallords.carbyne.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageManager {

//    private static Class<?> CRAFTPLAYERCLASS;
//    private static Class<?> PACKET_PLAYER_CHAT_CLASS;
//    private static Class<?> ICHATCOMP;
//    private static Class<?> CHATMESSAGE;
//    private static Class<?> PACKET_CLASS;
//    private static Constructor<?> PACKET_PLAYER_CHAT_CONSTRUCTOR;
//    private static Constructor<?> CHATMESSAGE_CONSTRUCTOR;
//    private static final String SERVER_VERSION;

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

    public static void broadcastMessage(String message) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void broadcastMessage(String message, String permission) {
        Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', message), permission);
    }

//    static {
//        String name = Bukkit.getServer().getClass().getName();
//        name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length()).substring(0, name.indexOf("."));
//        SERVER_VERSION = name;
//
//        try {
//            CRAFTPLAYERCLASS = Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + ".entity.CraftPlayer");
//            PACKET_PLAYER_CHAT_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".PacketPlayOutChat");
//            PACKET_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".Packet");
//            ICHATCOMP = Class.forName("net.minecraft.server." + SERVER_VERSION + ".IChatBaseComponent");
//            PACKET_PLAYER_CHAT_CONSTRUCTOR = Optional.of(PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, byte.class)).get();
//            CHATMESSAGE = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessage");
//            try {
//                CHATMESSAGE_CONSTRUCTOR = Optional.of(CHATMESSAGE.getConstructor(String.class, Object[].class)).get();
//            } catch (NoSuchMethodException e) {
//                CHATMESSAGE_CONSTRUCTOR = Optional.of(CHATMESSAGE.getDeclaredConstructor(String.class, Object[].class)).get();
//            }
//        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void sendHotBarMessage(Player player, String message) {
//        try {
//            Object icb = CHATMESSAGE_CONSTRUCTOR.newInstance(message, new Object[0]);
//            Object packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, (byte) 2);
//            Object craftplayerInst = CRAFTPLAYERCLASS.cast(player);
//            Optional<Method> methodOptional = Optional.of(CRAFTPLAYERCLASS.getMethod("getHandle"));
//            Object methodhHandle = methodOptional.get().invoke(craftplayerInst);
//            Object playerConnection = methodhHandle.getClass().getField("playerConnection").get(methodhHandle);
//            Optional.of(playerConnection.getClass().getMethod("sendPacket", PACKET_CLASS)).get().invoke(playerConnection, packet);
//        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//    }
}
