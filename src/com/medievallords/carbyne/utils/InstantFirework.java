package com.medievallords.carbyne.utils;

import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class InstantFirework extends EntityFireworks {

    private List<Player> players = new ArrayList<>();

    public InstantFirework(World world, List<Player> players) {
        super(world);
        this.players = players;
    }

    public InstantFirework(World world) {
        super(world);
        this.players = new ArrayList<>();
    }

    boolean gone = false;

    @Override
    public void t_() {
        if (gone) {
            return;
        }

        if (!this.world.isClientSide) {
            gone = true;

            if (players.size() > 0)
                for (Player player : players)
                    (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 17));
            else
                world.broadcastEntityEffect(this, (byte) 17);
            this.die();
        }
    }

    public static void spawn(Location location, List<Player> players, FireworkEffect... effect) {
        try {
            InstantFirework firework = new InstantFirework(((CraftWorld) location.getWorld()).getHandle(), players);
            FireworkMeta meta = ((Firework) firework.getBukkitEntity()).getFireworkMeta();
            for (FireworkEffect e : effect) {
                meta.addEffect(e);
            }
            ((Firework) firework.getBukkitEntity()).setFireworkMeta(meta);
            firework.setPosition(location.getX(), location.getY(), location.getZ());

            if ((((CraftWorld) location.getWorld()).getHandle()).addEntity(firework)) {
                firework.setInvisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void spawn(Location location, FireworkEffect... effect) {
        try {
            InstantFirework firework = new InstantFirework(((CraftWorld) location.getWorld()).getHandle());
            FireworkMeta meta = ((Firework) firework.getBukkitEntity()).getFireworkMeta();
            for (FireworkEffect e : effect) {
                meta.addEffect(e);
            }
            ((Firework) firework.getBukkitEntity()).setFireworkMeta(meta);
            firework.setPosition(location.getX(), location.getY(), location.getZ());

            if ((((CraftWorld) location.getWorld()).getHandle()).addEntity(firework)) {
                firework.setInvisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
