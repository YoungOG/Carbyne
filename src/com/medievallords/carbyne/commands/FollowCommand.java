package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Created by William on 7/12/2017.
 */
public class FollowCommand extends BaseCommand implements Listener {

    private HashMap<Player, Player> followers = new HashMap<>();

    @Command(name = "follow", permission = "carbyne.administrator", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /follow <player>");
            return;
        }

        String username = args[0];
        if (username.equalsIgnoreCase("off")) {
            if (!followers.containsKey(player)) {
                MessageManager.sendMessage(player, "&cYou are not following anyone");
                return;
            }

            PacketPlayOutCamera camera = new PacketPlayOutCamera(((CraftPlayer) player).getHandle());

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(camera);
            MessageManager.sendMessage(player, "&cYou stopped following &e" + followers.get(player).getName());
            followers.remove(player);
            return;
        }

        Player target = Bukkit.getServer().getPlayer(username);
        if (target == null) {
            MessageManager.sendMessage(player, "&cCould not find that player");
            return;
        }

        if (!getStaffManager().isVanished(player)) {
            getStaffManager().toggleVanish(player);
        }

        player.teleport(target);

        new BukkitRunnable() {
            @Override
            public void run() {
                toggleCamera(player, target);
            }
        }.runTaskLater(getCarbyne(), 60);

        followers.put(player, target);

        new BukkitRunnable() {

            Location last = target.getLocation();

            @Override
            public void run() {
                if (!followers.containsKey(player) || !followers.containsValue(target)) {
                    cancel();
                    return;
                }

                if (last.distance(target.getLocation()) > 7) {
                    last = target.getLocation();
                    player.teleport(target);
                }
            }
        }.runTaskTimer(getCarbyne(), 0, 10);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (followers.containsValue(event.getPlayer())) {
            for (Player player : followers.keySet()) {
                if (followers.get(player).equals(event.getPlayer())) {
                    PacketPlayOutCamera camera = new PacketPlayOutCamera(((CraftPlayer) player).getHandle());

                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(camera);
                    MessageManager.sendMessage(player, "&cYou stopped following &e" + followers.get(player).getName());
                    followers.remove(player);
                }
            }
        }

        if (followers.containsKey(event.getPlayer())) {
            followers.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (followers.containsValue(event.getPlayer())) {
            for (Player player : followers.keySet()) {
                if (followers.get(player).equals(event.getPlayer())) {
                    if (!getStaffManager().isVanished(player)) {
                        getStaffManager().toggleVanish(player);
                    }

                    player.teleport(followers.get(player));
                    camera(player);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            toggleCamera(player, followers.get(player));
                        }
                    }.runTaskLater(getCarbyne(), 60);
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (followers.containsValue(event.getPlayer())) {
            for (Player player : followers.keySet()) {
                if (followers.get(player).equals(event.getPlayer())) {
                    player.teleport(followers.get(player));
                    camera(player);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            toggleCamera(player, followers.get(player));
                        }
                    }.runTaskLater(getCarbyne(), 60);

                }
            }
        }
    }

    public void toggleCamera(Player player, Player target) {
        if (target == null) {
            return;
        }
        PacketPlayOutCamera camera = new PacketPlayOutCamera(((CraftPlayer) target).getHandle());

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(camera);
    }

    public void camera(Player player) {
        PacketPlayOutCamera camera = new PacketPlayOutCamera(((CraftPlayer) player).getHandle());

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(camera);
    }
}
