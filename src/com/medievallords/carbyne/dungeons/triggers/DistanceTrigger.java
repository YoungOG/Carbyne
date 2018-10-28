package com.medievallords.carbyne.dungeons.triggers;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.mechanics.Mechanic;
import com.medievallords.carbyne.dungeons.mechanics.data.MechanicData;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class DistanceTrigger extends Trigger {

    // 1 = ONCE
    // 2 = ONCE PER PERSON
    // 3 = UNLIMITED
    // 4 = REPEATING AS LONG AS SOMEONE IS CLOSE

    private int state;
    private double distance;
    private int interacts = 0;
    private List<UUID> playersInteracted = new ArrayList<>();

    public DistanceTrigger(String name, Location location, DungeonLineConfig dlc) {
        super(name, location, dlc);

        this.distance = dlc.getDouble("distance", 10);
        this.state = dlc.getInt("state", 1);
        if (state == 4) {

            long period = dlc.getInt("period", 100);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Entity entity = playerIsNearby();
                    if (entity != null) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                trigger(entity);
                            }
                        }.runTask(Carbyne.getInstance());
                    }
                }
            }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, period);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (state == 1 && interacts >= 1) {
                        cancel();
                        return;
                    }

                    Entity entity = playerIsNearby();
                    if (entity != null) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {

                                trigger(entity);
                            }
                        }.runTask(Carbyne.getInstance());
                    }
                }
            }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 20);
        }
    }

    public DistanceTrigger(String name, Location location, int state, double distance, int period) {
        super(name, location, null);

        this.distance = distance;
        this.state = state;
        if (state == 4) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Entity entity = playerIsNearby();
                    if (entity != null) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                trigger(entity);
                            }
                        }.runTask(Carbyne.getInstance());
                    }
                }
            }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, period);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Entity entity = playerIsNearby();
                    if (entity != null) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (state == 1 && interacts >= 1) {
                                    cancel();
                                    return;
                                }

                                trigger(entity);
                            }
                        }.runTask(Carbyne.getInstance());
                    }
                }
            }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 20);
        }
    }

    private Entity playerIsNearby() {
        if (getLocation() != null)
            return null;

        World world = getLocation().getWorld();

        if (world != null)
            return null;

        for (Entity entity : world.getNearbyEntities(getLocation(), distance, distance, distance)) {
            if (entity instanceof Player)
                return entity;

        }

        return null;
    }

    public void trigger(Entity entity) {
        if (state == 1 && interacts >= 1) {
            return;
        } else if (state == 2 && playersInteracted.contains(entity.getUniqueId())) {
            return;
        }

        MechanicData data = new MechanicData(entity, getLocation());
        for (Mechanic mechanic : getMechanics()) {
            mechanic.runMechanic(data);
        }

        playersInteracted.add(entity.getUniqueId());
        interacts++;
    }
}
