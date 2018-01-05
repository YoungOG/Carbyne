package com.medievallords.carbyne.tutorial.tasks;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

public class BoatExplodeTask extends BukkitRunnable {

    private Player player;
    private Location loc1 = new Location(Bukkit.getWorld("world"), -550, 59, -1577);
    private Location loc2 = new Location(Bukkit.getWorld("world"), -559, 54, -1595);
    private Location[] explodeLocations = new Location[]{new Location(Bukkit.getWorld("world"), -553, 56, -1578),
            new Location(Bukkit.getWorld("world"), -553, 52, -1579),
            new Location(Bukkit.getWorld("world"), -552, 56, -1584),
            new Location(Bukkit.getWorld("world"), -554, 60, -1585),
            new Location(Bukkit.getWorld("world"), -556, 56, -1588),
            new Location(Bukkit.getWorld("world"), -554, 55, -1587)
    };

    public BoatExplodeTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        sendOh();
        explode();
        for (double x = loc2.getX(); x <= loc1.getX(); x++) {
            for (double y = loc2.getY(); y <= loc1.getY(); y++) {
                for (double z = loc2.getZ(); z <= loc1.getZ(); z++) {
                    player.sendBlockChange(new Location(player.getWorld(), x, y, z), Material.AIR, (byte) 0);
                }
            }
        }
        wreck();

        RockFallTask task = new RockFallTask(player);
        task.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 20);
    }

    private void sendOh() {
        Title title = new Title.Builder().title("").subtitle("§c§lOh no! The boat.").stay(20).build();
        player.sendTitle(title);
    }

    private void wreck() {

    }

    private void explode() {
        player.playSound(explodeLocations[0], Sound.AMBIENCE_CAVE, 1, 0.8f);
        player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1, .75f);

        for (Location location : explodeLocations) {
            ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 1, location, true, player);
            player.playSound(player.getLocation(), Sound.ARROW_HIT, 1, .3f);

            if (Math.random() < 0.5) {
                player.playSound(location, Sound.FIREWORK_BLAST, 1, .65f);
            } else {
                player.playSound(location, Sound.EXPLODE, 1, (float) (Math.random() * 2));
            }
        }
    }
}
