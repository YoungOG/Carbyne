package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestBlockSpecial implements Special, Listener {

    private final List<Entity> entities = new ArrayList<>();

    public TestBlockSpecial() {
        Bukkit.getPluginManager().registerEvents(this, Carbyne.getInstance());
    }

    @Override
    public int getRequiredCharge() {
        return 100;
    }

    @Override
    public String getSpecialName() {
        return "TestSpecial";
    }

    @Override
    public void callSpecial(Player caster) {
        new BukkitRunnable() {
            final Location loc = caster.getLocation();
            final Vector vector = loc.getDirection().normalize();
            double t = 0;

            @Override
            public void run() {
                t += 3;
                final double x = vector.getX() * t;
                final double y = 0.0;
                final double z = vector.getZ() * t;
                loc.add(x, y, z);

                for (Location location : getBlocksInRadius(loc, 3)) {
                    FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location.add(0.1, 0, 0.1), location.getBlock().getType(), (byte) 0);
                    fallingBlock.setVelocity(new Vector(0, 1, 0));
                    entities.add(fallingBlock);
                }

                loc.subtract(x, y, z);

                if (t > 100) {
                    cancel();
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 5);
    }

    @EventHandler
    public void onLand(final EntityChangeBlockEvent event) {
        if (entities.contains(event.getEntity())) {
            event.setCancelled(true);
            entities.remove(event.getEntity());
        }
    }

    private final List<Location> getBlocksInRadius(final Location l, final int radius) {
        List<Location> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    final Location newloc = new Location(l.getWorld(), l.getX() + x, l.getY() + y, l.getZ() + z);
                    if (newloc.getBlock().getType().isSolid() && newloc.distance(l) <= radius) {
                        blocks.add(newloc);
                    }
                }
            }
        }

        Collections.shuffle(blocks);
        blocks = blocks.subList(0, blocks.size() / 2);
        return blocks;
    }
}
