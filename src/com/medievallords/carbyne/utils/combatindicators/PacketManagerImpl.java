package com.medievallords.carbyne.utils.combatindicators;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.Random;

public class PacketManagerImpl extends PacketManager {

    private static Random random;
    private Plugin plugin;

    public PacketManagerImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    private static Packet destoryPacket(int... entityIDs) {
        return new PacketPlayOutEntityDestroy(entityIDs);
    }

    private static Packet spawnArmorStandPacket(String customName, Location loc, int entityID) throws Exception {
        PacketPlayOutSpawnEntityLiving armorStandPacket = new PacketPlayOutSpawnEntityLiving();
        setPrivateField(armorStandPacket, "a", entityID);
        setPrivateField(armorStandPacket, "b", 30);
        setPrivateField(armorStandPacket, "c", (int) Math.floor(loc.getX() * 32.0));
        setPrivateField(armorStandPacket, "d", (int) Math.floor((loc.getY() - 2.1) * 32.0));
        setPrivateField(armorStandPacket, "e", (int) Math.floor(loc.getZ() * 32.0));
        DataWatcher armorStandDataWatcher = new DataWatcher(null);
        armorStandDataWatcher.a(0, (byte) 32);
        armorStandDataWatcher.a(2, customName);
        armorStandDataWatcher.a(3, (byte) 1);
        armorStandDataWatcher.a(10, (byte) 15);
        setPrivateField(armorStandPacket, "l", armorStandDataWatcher);
        return armorStandPacket;
    }

    private static void sendPackets(Location nearTo, Packet... packets) {
        for (Player player : nearTo.getWorld().getPlayers()) {
            if (nearTo.getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
                if (nearTo.distanceSquared(player.getLocation()) < 1024.0) {
                    PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;

                    if (conn == null) {
                        continue;
                    }

                    for (Packet packet : packets) {
                        if (packet != null) {
                            conn.sendPacket(packet);
                        }
                    }
                }
            }
        }
    }

    public static void setPrivateField(Object obj, String fieldName, Object newValue) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, newValue);
    }

    @Override
    public void sendDamageIndicator(Entity nearTo, Location loc, String name, boolean move, int hideAfterTicks) {
        int passengerID = FakeEntityIDs.next();
        int vehicleID = FakeEntityIDs.next();

        try {
            Packet armorStandPacket = spawnArmorStandPacket(name, loc, passengerID);
            Location center = nearTo.getLocation();
            sendPackets(center, armorStandPacket);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Packet destroyPacket = destoryPacket(vehicleID, passengerID);
                    sendPackets(center, destroyPacket);
                }
            }.runTaskLater(plugin, (long) hideAfterTicks);
        } catch (Exception ex) {
            ex.printStackTrace();
            plugin.getLogger().severe("Unable to send damage indicators, please report this error!");
        }
    }
}
