package com.medievallords.carbyne.tutorial.tasks;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.tutorial.TutorialManager;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RockFallTask extends BukkitRunnable {

    private final Player player;
    private boolean hasPassed = false;
    private final List<Location> locations = new ArrayList<>();
    private final List<Location> checkFor = new ArrayList<>();

    private final List<Entity> entities = new ArrayList<>();

    public RockFallTask(Player player) {
        this.player = player;

        checkFor.add(new Location(Bukkit.getWorld("world"), -553, 53, -1565));
        checkFor.add(new Location(Bukkit.getWorld("world"), -556, 53, -1568));
        checkFor.add(new Location(Bukkit.getWorld("world"), -558, 53, -1570));
        checkFor.add(new Location(Bukkit.getWorld("world"), -560, 53, -1572));

        locations.add(new Location(Bukkit.getWorld("world"), -556, 58, -1559));
        locations.add(new Location(Bukkit.getWorld("world"), -556, 59, -1560));
        locations.add(new Location(Bukkit.getWorld("world"), -557, 59, -1560));
        locations.add(new Location(Bukkit.getWorld("world"), -557, 58, -1559));
        locations.add(new Location(Bukkit.getWorld("world"), -557, 60, -1561));
        locations.add(new Location(Bukkit.getWorld("world"), -558, 60, -1561));
        locations.add(new Location(Bukkit.getWorld("world"), -558, 60, -1562));
        locations.add(new Location(Bukkit.getWorld("world"), -559, 60, -1563));
        locations.add(new Location(Bukkit.getWorld("world"), -560, 60, -1563));
        locations.add(new Location(Bukkit.getWorld("world"), -561, 62, -1564));
        locations.add(new Location(Bukkit.getWorld("world"), -561, 62, -1565));
        locations.add(new Location(Bukkit.getWorld("world"), -562, 63, -1565));
        locations.add(new Location(Bukkit.getWorld("world"), -562, 63, -1566));
        locations.add(new Location(Bukkit.getWorld("world"), -562, 63, -1567));
        locations.add(new Location(Bukkit.getWorld("world"), -563, 65, -1567));

        Collections.shuffle(locations);

        //new Location(Bukkit.getWorld("world"), -551, 54, -1564), new Location(Bukkit.getWorld("world"), -551, 54, -1564)};
        //new Location(Bukkit.getWorld("world"), -551, 54, -1564), new Location(Bukkit.getWorld("world"), -551, 54, -1564)};
        TutorialManager.rockFallTasks.add(this);

    }

    @Override
    public void run() {
        if (isNear()) {
            sendWarning();
            fall();
            new BukkitRunnable() {
                @Override
                public void run() {
                    CollapseTask task = new CollapseTask(player);
                    task.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 20);
                }
            }.runTaskLater(Carbyne.getInstance(), 35);
        }
    }

    private void sendWarning() {
        Title title = new Title.Builder().title("").subtitle("§c§lWatch out!").stay(20).build();
        player.sendTitle(title);
        player.playSound(player.getLocation(), Sound.AMBIENCE_CAVE, 1, 2);
        player.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, .2f);
    }

    private void fall() {
        final Iterator<Location> locs = locations.iterator();
        player.playSound(locations.get(0), Sound.MINECART_BASE, 1f, 0.5f);
        player.playSound(locations.get(0), Sound.AMBIENCE_THUNDER, 1f, 1.67f);
        player.playSound(locations.get(0), Sound.IRONGOLEM_WALK, 1f, 0.3f);
        player.playSound(player.getLocation(), Sound.ZOMBIE_REMEDY, 1f, 0.2f);
        new BukkitRunnable() {
            public void run() {
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 8, 5));
            }
        }.runTask(Carbyne.getInstance());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (locs.hasNext()) {
                    displayFalling(locs.next());
                } else {
                    cancel();
                    player.playSound(locations.get(0), Sound.IRONGOLEM_WALK, 1f, 0.4f);
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 3);

        player.playSound(locations.get(0), Sound.IRONGOLEM_WALK, 1f, 0.3f);
        Collections.shuffle(locations);
        final Iterator<Location> locs2 = locations.iterator();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (locs2.hasNext()) {
                    displayFalling(locs2.next());
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 4);
    }

    private void displayFalling(Location location) {
        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.SANDSTONE, (byte) 0), 0, -0.3f, 0, 0, 3, location, true, player);
        ParticleEffect.SMOKE_NORMAL.display(0, -0.3f, 0, 0, 2, location, true, player);

        new BukkitRunnable() {
            public void run() {
                final FallingBlock f = player.getWorld().spawnFallingBlock(location, Material.STONE, (byte) 0);
                Carbyne.getInstance().getTutorialManager().hideEntity(f, player);
                entities.add(f);
            }
        }.runTask(Carbyne.getInstance());

        ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.STONE, (byte) 0), 0, 0, 0, 0, 3, location, true, player);

    }

    public void spawnRock(Location location) {
        player.sendBlockChange(location, Material.STONE, (byte) 0);
        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.SANDSTONE, (byte) 0), 0, 0, 0, 0, 3, location, true, player);
        ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, 0, 2, location, true, player);
        ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.STONE, (byte) 0), 0, 0, 0, 0, 3, location, true, player);
    }

    private boolean isNear() {
        if (!hasPassed) {
            for (Location location : checkFor) {
                if (player.getWorld().equals(location.getWorld()) && player.getLocation().distance(location) <= 3.5) {
                    hasPassed = true;
                    return true;
                }
            }
        }

        return false;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Player getPlayer() {
        return player;
    }
}
