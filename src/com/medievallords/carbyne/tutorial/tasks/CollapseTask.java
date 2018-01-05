package com.medievallords.carbyne.tutorial.tasks;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.tutorial.TutorialManager;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CollapseTask extends BukkitRunnable {

    private final Player player;
    private final Location checkFor = new Location(Bukkit.getWorld("world"), -597, 53, -1556);
    private boolean hasPassed = false;
    private final List<Location> locations = new ArrayList<>();
    private final Random random = new Random();

    private final List<Entity> entities = new ArrayList<>();

    public CollapseTask(Player player) {
        this.player = player;

        locations.add(new Location(Bukkit.getWorld("world"), -590, 62, -1562));
        locations.add(new Location(Bukkit.getWorld("world"), -588, 61, -1561));
        locations.add(new Location(Bukkit.getWorld("world"), -586, 59, -1557));
        locations.add(new Location(Bukkit.getWorld("world"), -593, 58, -1559));
        locations.add(new Location(Bukkit.getWorld("world"), -559, 57, -1559));
        locations.add(new Location(Bukkit.getWorld("world"), -603, 62, -1556));
        locations.add(new Location(Bukkit.getWorld("world"), -600, 61, -1567));
        locations.add(new Location(Bukkit.getWorld("world"), -602, 63, -1556));
        locations.add(new Location(Bukkit.getWorld("world"), -597, 65, -1563));
        locations.add(new Location(Bukkit.getWorld("world"), -559, 57, -1559));
        locations.add(new Location(Bukkit.getWorld("world"), -559, 57, -1561));
        locations.add(new Location(Bukkit.getWorld("world"), -596, 65, -1559));
        locations.add(new Location(Bukkit.getWorld("world"), -596, 65, -1561));
        locations.add(new Location(Bukkit.getWorld("world"), -602, 62, -1555));
        locations.add(new Location(Bukkit.getWorld("world"), -607, 61, -1553));
        locations.add(new Location(Bukkit.getWorld("world"), -595, 61, -1554));
        locations.add(new Location(Bukkit.getWorld("world"), -605, 62, -1553));
        locations.add(new Location(Bukkit.getWorld("world"), -607, 61, -1556));
        locations.add(new Location(Bukkit.getWorld("world"), -609, 61, -1552));
        locations.add(new Location(Bukkit.getWorld("world"), -599, 62, -1565));
        locations.add(new Location(Bukkit.getWorld("world"), -589, 62, -1578));
        locations.add(new Location(Bukkit.getWorld("world"), -609, 61, -1552));
        locations.add(new Location(Bukkit.getWorld("world"), -589, 63, -1574));
        locations.add(new Location(Bukkit.getWorld("world"), -589, 63, -1572));
        locations.add(new Location(Bukkit.getWorld("world"), -589, 63, -1569));
        locations.add(new Location(Bukkit.getWorld("world"), -585, 63, -1573));
        locations.add(new Location(Bukkit.getWorld("world"), -572, 61, -1570));
        locations.add(new Location(Bukkit.getWorld("world"), -576, 62, -1567));
        locations.add(new Location(Bukkit.getWorld("world"), -609, 61, -1552));
        locations.add(new Location(Bukkit.getWorld("world"), -574, 62, -1567));

        TutorialManager.collapseTasks.add(this);
    }

    @Override
    public void run() {
        if (isNear()) {
            startFalling();
        }
    }

    private void startFalling() {
        new BukkitRunnable() {
            @Override
            public void run() {
                displayFalling(locations.get(random.nextInt(locations.size())));
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 14);
    }

    private void displayFalling(Location location) {
        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.SANDSTONE, (byte) 0), 0, -0.3f, 0, 0, 3, location, true, player);
        ParticleEffect.SMOKE_NORMAL.display(0, -0.3f, 0, 0, 2, location, true, player);

        FallingBlock f = player.getWorld().spawnFallingBlock(location, Material.STONE, (byte) 0);
        Carbyne.getInstance().getTutorialManager().hideEntity(f, player);

        entities.add(f);

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
            if (player.getWorld().equals(checkFor.getWorld()) && player.getLocation().distance(checkFor) <= 8) {
                hasPassed = true;
                return true;
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
