package com.medievallords.carbyne.gates.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gates.Gate;
import com.medievallords.carbyne.utils.StaticClasses;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Calvin on 2/4/2017
 * for the Carbyne-Gear project.
 */
public class GateMobListeners implements Listener {


    @EventHandler
    public void onEntitySpawn(MythicMobSpawnEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ActiveMob mob = MythicMobs.inst().getMobManager().getMythicMobInstance(event.getEntity());
                if (mob != null)
                    if (mob.getSpawner() != null)
                        for (Gate gate : StaticClasses.gateManager.getGates())
                            for (MythicSpawner spawner : gate.getMythicSpawners().values())
                                if (spawner != null)
                                    if (spawner.getInternalName().equalsIgnoreCase(mob.getSpawner().getInternalName())) {
                                        gate.addMob();
                                        return;
                                    }
            }
        }.runTaskLater(Carbyne.getInstance(), 5L);
    }

    @EventHandler
    public void onEntityDeath(MythicMobDeathEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ActiveMob mob = MythicMobs.inst().getMobManager().getMythicMobInstance(event.getEntity());

                if (mob != null)
                    if (mob.getSpawner() != null)
                        for (Gate gate : StaticClasses.gateManager.getGates())
                            for (MythicSpawner spawner : gate.getMythicSpawners().values())
                                if (spawner != null)
                                    if (spawner == mob.getSpawner()) {
                                        gate.killMob();
                                        return;
                                    }
            }
        }.runTaskLater(Carbyne.getInstance(), 5L);
    }
}
