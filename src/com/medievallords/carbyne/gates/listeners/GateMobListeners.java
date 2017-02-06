package com.medievallords.carbyne.gates.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.gates.GateManager;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobSpawnEvent;
import net.elseland.xikage.MythicMobs.Mobs.ActiveMob;
import net.elseland.xikage.MythicMobs.MythicMobs;
import net.elseland.xikage.MythicMobs.Spawners.MythicSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Calvin on 2/4/2017
 * for the Carbyne-Gear project.
 */
public class GateMobListeners implements Listener {

    private Carbyne carbyne = Carbyne.getInstance();
    private GateManager gateManager = carbyne.getGateManager();

    @EventHandler
    public void onEntitySpawn(MythicMobSpawnEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ActiveMob mob = MythicMobs.inst().getAPI().getMobAPI().getMythicMobInstance(event.getEntity());
                if (mob != null) {
                    if (mob.getSpawner() != null) {
                        for (Gate gate : gateManager.getGates()) {
                            for (MythicSpawner spawner : gate.getMythicSpawners().values()) {
                                if (spawner.getInternalName().equalsIgnoreCase(mob.getSpawner().getInternalName())) {
                                    gate.addMob();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskLater(carbyne, 5L);
    }

    @EventHandler
    public void onEntityDeath(MythicMobDeathEvent event) {
        ActiveMob mob = MythicMobs.inst().getAPI().getMobAPI().getMythicMobInstance(event.getEntity());

        if (mob != null) {
            if (mob.getSpawner() != null) {
                for (Gate gate : gateManager.getGates()) {
                    for (MythicSpawner spawner : gate.getMythicSpawners().values()) {
                        if (spawner == mob.getSpawner()) {
                            gate.killMob();
                            return;
                        }
                    }
                }
            }
        }
    }
}
